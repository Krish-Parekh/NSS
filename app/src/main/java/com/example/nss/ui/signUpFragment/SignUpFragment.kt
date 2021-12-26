package com.example.nss.ui.signUpFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nss.R
import com.example.nss.databinding.FragmentSignUpBinding
import com.example.nss.model.User
import com.example.nss.utils.dismissDialogBox
import com.example.nss.utils.getDialogBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

private const val TAG = "SIGNUP"

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseStorage: FirebaseStorage
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    var selectedPhotoUri: Uri? = null
    private var core: Boolean? = false
    private var member: Boolean? = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //TODO:Firebase Initialisation
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseStorage = FirebaseStorage.getInstance()


        binding.btnPhotoPick.setOnClickListener {
            getPhoto()
        }
        binding.circularImage.setOnClickListener {
            getPhoto()
        }


        binding.btnRegister.setOnClickListener {
            getDialogBox(requireContext())
            binding.btnRegister.isEnabled = false
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            checkButton()

            if (verifySignUpCredentials(username, email, password, core, member)) {
                authenticate(username, email, password)
            } else {
                Log.i(TAG, "Please enter your credentials properly")
            }

            clearField()
        }

    }

    private fun clearField() {
        binding.apply {
            etEmail.text.clear()
            etPassword.text.clear()
            etUsername.text.clear()
            binding.circularImage.setImageBitmap(null)
            binding.btnPhotoPick.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = true
        }
    }

    //TODO: Authenticate user and save user data inside database
    private fun authenticate(username: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadImageToFireStorage(username, email, mAuth.currentUser?.uid!!)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error : $it", Toast.LENGTH_SHORT).show()
                dismissDialogBox()
            }
    }


    //TODO: Upload image to FireStorage and taking image url
    private fun uploadImageToFireStorage(username: String, email: String, uid: String) {
        if (selectedPhotoUri == null) return
        val fileName = UUID.randomUUID().toString()
        val ref = mFirebaseStorage.getReference("/images/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveUserToDatabase(username, email, uid, uri)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error : $it", Toast.LENGTH_SHORT).show()
                dismissDialogBox()
            }
    }


    //TODO: Saving user details in RT_Database
    private fun saveUserToDatabase(username: String, email: String, uid: String, photoUri: Uri?) {
        val ref = mFirebaseDatabase.getReference("users/$uid")
        val user = User(username, email, photoUri.toString(), uid, core, member)
        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "SignUp Success", Toast.LENGTH_SHORT).show()
                goToBaseFragment()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error : $it", Toast.LENGTH_SHORT).show()
                dismissDialogBox()
            }
    }

    private fun goToBaseFragment() {
        dismissDialogBox()
        findNavController().navigate(R.id.action_signUpFragment_to_baseFragment)
    }

    //TODO: Verify Sign Up Credentials
    private fun verifySignUpCredentials(
        username: String,
        email: String,
        password: String,
        core: Boolean?,
        member: Boolean?
    ): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && (core!! || member!!)
    }


    // TODO: Check Button For Core and Member Selection inside
    private fun checkButton() {
        val radioId = binding.radioGroup.checkedRadioButtonId
        val radioButton = requireActivity().findViewById<RadioButton>(radioId)
        if (radioButton.text == "Core Member") {
            core = true
            member = false
            return
        } else {
            core = false
            member = true
        }
    }

    //TODO: Getting Photo From Gallery
    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireContext().contentResolver,
                selectedPhotoUri
            )
            binding.btnPhotoPick.visibility = View.INVISIBLE
            binding.circularImage.setImageBitmap(bitmap)
        }
    }

}