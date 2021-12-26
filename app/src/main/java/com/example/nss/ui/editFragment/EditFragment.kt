package com.example.nss.ui.editFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nss.R
import com.example.nss.databinding.FragmentEditBinding
import com.example.nss.model.Activity
import com.example.nss.utils.getDialogBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*


private const val TAG = "EditFragment"

class EditFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentEditBinding
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    var year = 0
    var month = 0
    var day = 0
    var hours = 0
    var minutes = 0
    private var dateSeq: String = "Selected Date"
    private var timeSeq: String = "Selected Time"
    private val args by navArgs<EditFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFirebaseDatabase = FirebaseDatabase.getInstance()

        binding.apply {
            etEventName.setText(args.edit.activityName)
            etDescription.setText(args.edit.activityDescription)

            dateSeq = args.edit.activityDate.toString()
            timeSeq = args.edit.activityTime.toString()

            tvDate.text = dateSeq
            tvTime.text = "$timeSeq, Onwards"
            etLink.setText(args.edit.link)
        }


        binding.btnRegisterActivity.setOnClickListener {

            binding.apply {
                val activityName = etEventName.text.toString()
                val activityIdentity = activityIdentity.selectedItem.toString()
                val activityDesc = etDescription.text.toString()
                val selectedDate = dateSeq
                val selectedTime = timeSeq
                val activityLink = etLink.text.toString()
                val creationTimeMs = args.cTime
                val activityModel: Activity
                if (verifyEvent(activityName,activityIdentity,activityDesc,selectedDate,selectedTime,activityLink)) {
                    activityModel = Activity(activityName,activityIdentity,activityDesc,selectedDate,selectedTime,activityLink,creationTimeMs)
                    updateIntoDatabase(activityModel)
                }
            }
            clearTextField()
        }
        binding.ivDate.setOnClickListener {
            //Logic for date
            getInstance()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.ivTime.setOnClickListener {
            //Logic for time
            getInstance()
            TimePickerDialog(requireContext(), this, hours, minutes, false).show()
        }

    }

    private fun updateIntoDatabase(activityModel: Activity) {
        val ref = mFirebaseDatabase.reference.child("event").child(args.cTime)
        Log.i(TAG, "creation time : ${args.cTime}")
        ref.setValue(activityModel)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Your activity updated success", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editFragment_to_homeFragment)
            }
    }

    private fun verifyEvent(activityName: String,activityIdentity: String,activityDesc: String,selectedDate: String,selectedTime: String,activityLink: String): Boolean {
        return when {
            activityName.isNotEmpty() && activityIdentity.isNotEmpty() && activityDesc.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty() && activityLink.isNotEmpty() -> true
            else -> {
                Toast.makeText(requireContext(), "Enter each and every field", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    private fun clearTextField() {
        binding.apply {
            etEventName.text.clear()
            etDescription.text.clear()
            etLink.text.clear()
            tvDate.text = "Select Date"
            tvTime.text = "Select Time"
        }
    }

    private fun getInstance() {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hours = cal.get(Calendar.HOUR)
        minutes = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        dateSeq = DateFormat.format("EEEE, dd MMM yyy", cal) as String
        binding.tvDate.text = dateSeq
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        timeSeq = DateFormat.format("hh:mm a", cal) as String
        binding.tvTime.text = "$timeSeq, Onwards"
    }
}