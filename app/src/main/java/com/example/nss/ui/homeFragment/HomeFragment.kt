package com.example.nss.ui.homeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nss.databinding.FragmentHomeBinding
import com.example.nss.model.Activity
import com.example.nss.ui.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), RvButton {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mActivityList: ArrayList<Activity> = ArrayList()
    private lateinit var adapter: ActivityAdapter
    private var core: Boolean = false
    lateinit var model: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance().reference

        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.core.observe(viewLifecycleOwner, Observer {
            val recyclerView = binding.rvActivity
            core = it
            adapter = ActivityAdapter(requireContext(), mActivityList, this@HomeFragment, it)
            val alphaAdapter = AlphaInAnimationAdapter(adapter)
            recyclerView.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
                setDuration(1000)
                setHasStableIds(false)
                setFirstOnly(false)
                setInterpolator(OvershootInterpolator(.100f))
            }
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            getActivityList()
        })



    }

    private fun getActivityList() {
        mFirebaseDatabase.child("event").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mActivityList.clear()
                for (activitySnapShot in snapshot.children) {
                    val currAct = activitySnapShot.getValue(Activity::class.java)
                    mActivityList.add(currAct!!)
                }
                if (mActivityList.isEmpty()) {
                    binding.lottieAnimation.visibility = View.VISIBLE
                    binding.tvEmptyList.visibility = View.VISIBLE
                } else {
                    binding.lottieAnimation.visibility = View.INVISIBLE
                    binding.tvEmptyList.visibility = View.INVISIBLE
                }
                mActivityList.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("db", "$error")
            }
        })
    }

    override fun onDeleteClick(creationTime: String, position: Int) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Delete Activity")
            .setMessage("Are you sure you want to delete ${mActivityList[position].activityName} activity?")
            .setPositiveButton("Yes") { _, _ ->
                deleteDataFromDB(creationTime)
                Toast.makeText(
                    requireContext(),
                    "${mActivityList[position].activityName} activity deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }

    private fun deleteDataFromDB(time: Any) {
        val query = mFirebaseDatabase.child("event").child(time.toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.ref.removeValue()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onEditClick(creationTime: String, bundle: Activity) {
        Log.d(TAG, "creation time : $creationTime")
        val action = HomeFragmentDirections.actionHomeFragmentToEditFragment(bundle, creationTime)
        findNavController().navigate(action)
    }

    override fun enrolClick(bundle: Activity) {
        val action = HomeFragmentDirections.actionHomeFragmentToEnrol(bundle, core, "")
        findNavController().navigate(action)
    }
}