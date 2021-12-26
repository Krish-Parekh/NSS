package com.example.nss.ui.loginFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nss.R
import com.example.nss.databinding.FragmentLoginBinding
import com.example.nss.utils.dismissDialogBox
import com.example.nss.utils.getDialogBox
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "LOGIN"
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topAnim = AnimationUtils.loadAnimation(requireContext(),R.anim.fade_in)
        binding.ivLogin.animation = topAnim
        activity?.actionBar?.hide()

        //TODO:Firebase Initialisation
        mAuth = FirebaseAuth.getInstance()


        if (mAuth.currentUser != null){
            getDialogBox(requireContext())
            gotoBaseFragment()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim{it <= ' '}
            val password = binding.etPassword.text.toString().trim{it <= ' '}
            getDialogBox(requireContext())
            if(verifyLoginCredentials(email,password)) {
                signInUser(email, password)
            }else{
                Toast.makeText(requireContext(), "Please enter you credentials properly", Toast.LENGTH_SHORT).show()
            }

            binding.etEmail.text.clear()
            binding.etPassword.text.clear()
        }

        //TODO:SignUp Button Navigation
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun verifyLoginCredentials(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun signInUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener { authResult ->
                Log.i(TAG,"User Signed in Success $authResult")
                gotoBaseFragment()
            }
            .addOnFailureListener { exception ->
                dismissDialogBox()
                Log.i(TAG,"Some error as occurred $exception")
            }
    }

    private fun gotoBaseFragment() {
        dismissDialogBox()
        findNavController().navigate(R.id.action_loginFragment_to_baseFragment)
    }
}