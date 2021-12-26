package com.example.nss.ui.teamFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nss.databinding.FragmentTeamBinding
import com.example.nss.model.User
import com.example.nss.utils.dismissDialogBox
import com.example.nss.utils.getDialogBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

private const val TAG = "TeamFragment"

class TeamFragment : Fragment() {
    private lateinit var binding: FragmentTeamBinding
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserList: ArrayList<User>
    private lateinit var adapter: TeamAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG,"WE ARE INSIDE TEAM FRAGMENT")

        //TODO: Firebase initialisation
        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

        // TODO: Recycler view setup for userList
        val rvTeam = binding.rvTeam
        mUserList = ArrayList()
        adapter = TeamAdapter(requireContext(), mUserList)
        val alphaAdapter = AlphaInAnimationAdapter(adapter)
        rvTeam.adapter =  ScaleInAnimationAdapter(alphaAdapter).apply {
            setDuration(1000)
            setHasStableIds(false)
            setFirstOnly(false)
            setInterpolator(OvershootInterpolator(.100f))
        }
        rvTeam.layoutManager = LinearLayoutManager(requireContext())



        // TODO: Fetching user data from firebase
        mDatabaseReference.child("users").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapShot in snapshot.children) {
                    val user = postSnapShot.getValue(User::class.java)
                    mUserList.add(user!!)
                    Log.i(TAG, "User : $user")
                }
                dismissDialogBox()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                dismissDialogBox()
            }
        })
    }



}