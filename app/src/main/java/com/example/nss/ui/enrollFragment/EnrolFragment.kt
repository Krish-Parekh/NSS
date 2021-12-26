package com.example.nss.ui.enrollFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nss.R
import com.example.nss.databinding.FragmentEnrolBinding
import com.example.nss.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

private const val TAG = "EnrolFragment"

class EnrolFragment : Fragment(), BtnRole {

    private lateinit var binding: FragmentEnrolBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: DatabaseReference
    private val args by navArgs<EnrolFragmentArgs>()
    private lateinit var mEnrolUser: ArrayList<User>
    private lateinit var adapter: EnrolAdapter
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnrolBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initializing Firebase variable
        setUpView()
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance().reference
        mEnrolUser = ArrayList()

        // Setting up Recycler View
        val rvEnrol = binding.rvEnrol
        adapter = EnrolAdapter(requireContext(), mEnrolUser, args.position, this@EnrolFragment)
        val alphaAdapter = AlphaInAnimationAdapter(adapter)
        rvEnrol.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
            setDuration(1000)
            setHasStableIds(false)
            setFirstOnly(false)
            setInterpolator(OvershootInterpolator(.100f))
        }
        rvEnrol.layoutManager = LinearLayoutManager(requireContext())


        // Getting users who have enrolled for the activity
        val ref = mFirebaseDatabase.child("enrol").child(args.enrol.activityName!!)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mEnrolUser.clear()
                for (postSnapShot in snapshot.children) {
                    user = postSnapShot.getValue(User::class.java)!!
                    mEnrolUser.add(user)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // Adding current user to database
        binding.btnAttend.setOnClickListener {
            binding.btnAttend.isEnabled = false
            getCurrentUser()
            binding.btnAttend.isEnabled = true
        }

    }

    private fun getCurrentUser() {
        val userUID = mAuth.currentUser?.uid.toString()
        val ref = mFirebaseDatabase.child("users").child(userUID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)!!
                putCurrentUserInDatabase(currentUser)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun putCurrentUserInDatabase(currentUser: User) {
        val currentUserUID = mAuth.currentUser?.uid.toString()
        val ref = mFirebaseDatabase.child("enrol").child(args.enrol.activityName!!).child(currentUserUID)
        ref.setValue(currentUser)
            .addOnSuccessListener {
            }
    }


    private fun setUpView() {
        binding.activityName.text = args.enrol.activityName
        binding.activityDescription.text = args.enrol.activityDescription
        binding.tvTime.text = args.enrol.activityTime
        binding.tvDate.text = args.enrol.activityDate
    }


    // Using BottomSheet to assign task like (Email Photo , Email Report) and only if current user is core member he can assign task to other users
    override fun assignTask(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom_sheet)
        val button = bottomSheetDialog.findViewById<MaterialButton>(R.id.addTask)

        button?.setOnClickListener {
            val radioGroup = bottomSheetDialog.findViewById<RadioGroup>(R.id.radioGroup)
            val radioId = radioGroup?.checkedRadioButtonId!!
            val radioButton = bottomSheetDialog.findViewById<RadioButton>(radioId)


            if (radioButton?.text == "Email Report") {
                updateUser(Ereport = true,Ephoto = false,uid = mEnrolUser[position].uid!!,position = position)
            } else {
                updateUser(Ereport = false,Ephoto = true,uid = mEnrolUser[position].uid!!,position = position)
            }
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.show()
    }

    private fun updateUser(Ereport: Boolean, Ephoto: Boolean, uid: String, position: Int) {
        val ref = mFirebaseDatabase.child("enrol").child(args.enrol.activityName!!).child(uid)
        val updatedUser = mEnrolUser[position]
        updatedUser.Ereport = Ereport
        updatedUser.Ephoto = Ephoto
        ref.setValue(updatedUser)
            .addOnSuccessListener {
                Log.i(TAG, "Task Assigned Success")
            }
    }

    override fun deleteUser(uid: String, name: String) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Delete User")
            .setMessage("Are you sure you want to remove $name from activity?")
            .setPositiveButton("Yes") { _, _ ->
                deleteDataFromDB(uid)
                Toast.makeText(
                    requireContext(),
                    "$name removed from activity",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()

    }

    private fun deleteDataFromDB(uid: String) {
        val query = mFirebaseDatabase.child("enrol").child(args.enrol.activityName!!).child(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.ref.removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

}
