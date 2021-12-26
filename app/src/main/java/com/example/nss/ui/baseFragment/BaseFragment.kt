package com.example.nss.ui.baseFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nss.R
import com.example.nss.databinding.FragmentBaseBinding
import com.example.nss.model.User
import com.example.nss.ui.SharedViewModel
import com.example.nss.utils.dismissDialogBox
import com.example.nss.utils.getDialogBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private const val TAG = "BaseFragment"

class BaseFragment : Fragment() {
    private lateinit var binding: FragmentBaseBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.base_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_menu -> {
                signOut()
                true
            }
            else -> false
        }
    }

    private fun signOut() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Logout")
            .setMessage("Are you sure you want to Logout?")
            .setPositiveButton("Yes") { _, _ ->
                mAuth.signOut()
                findNavController().navigate(R.id.action_baseFragment_to_loginFragment)
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        getDialogBox(requireContext())

        // Getting current user role to change View (core : getsToAssignActivity , member : canJustSeeTheActivity) role base access
        val currentUser = mAuth.currentUser?.uid.toString()
        mDatabase.child("users").child(currentUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val core = snapshot.getValue(User::class.java)?.core
                    model.sendMessage(core!!)
                    getFloatingActionBtn(core!!)
                    dismissDialogBox()
                }

                override fun onCancelled(error: DatabaseError) {
                    dismissDialogBox()
                }

            })


        // To get equal space and remove background from bottom navigation
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(1).isEnabled = true

        // as we are nesting fragment we are inside childFragment, we are attaching navController with our bottom navigation
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_baseFragment_to_activityFragment)
        }

    }

    private fun getFloatingActionBtn(core: Boolean) {
        if (core) {
            binding.fab.visibility = View.VISIBLE
        } else {
            binding.fab.visibility = View.GONE
            binding.coordinatorLayout.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        dismissDialogBox()
    }


}