package com.example.nss.ui.activityFragment

import com.example.nss.model.Activity
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
import androidx.navigation.fragment.findNavController
import com.example.nss.R
import com.example.nss.databinding.FragmentActivityBinding
import com.example.nss.utils.dismissDialogBox
import com.example.nss.utils.getDialogBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

private const val TAG = "ActivityFragment"
class ActivityFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentActivityBinding
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mAuth : FirebaseAuth
    var year = 0
    var month = 0
    var day = 0
    var hours = 0
    var minutes = 0
    private var dateSeq : String = "Selected Date"
    private var timeSeq : String = "Selected Time"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()


        binding.btnRegisterActivity.setOnClickListener {
            getDialogBox(requireContext())

            binding.apply {
                val activityName = etEventName.text.toString()
                val activityIdentity = activityIdentity.selectedItem.toString()
                val activityDesc = etDescription.text.toString()
                val selectedDate = dateSeq
                val selectedTime = timeSeq
                val activityLink = etLink.text.toString()
                val creationTimeMs = System.currentTimeMillis().toString()
                val activityModel : Activity
                if (verifyEvent(activityName, activityIdentity, activityDesc, selectedDate, selectedTime, activityLink)){
                    activityModel = Activity(activityName, activityIdentity, activityDesc, selectedDate, selectedTime, activityLink,creationTimeMs)
                    insertIntoFirebaseDatabase(activityModel)
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

    private fun backToBaseFragment() {
        findNavController().navigate(R.id.action_activityFragment_to_baseFragment)
    }

    private fun insertIntoFirebaseDatabase(activityModel: Activity) {
        val uid = activityModel.creation_time_ms
        val ref = mFirebaseDatabase.getReference("event/$uid")
        ref.setValue(activityModel)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Activity is registered",Toast.LENGTH_SHORT).show()
                dismissDialogBox()
                backToBaseFragment()
                Log.i(TAG,"Yeh this is working")
            }
    }

    private fun verifyEvent(activityName: String, activityIdentity: String, activityDesc: String, selectedDate: String, selectedTime: String, activityLink: String): Boolean {
        return when{
            activityName.isNotEmpty() && activityIdentity.isNotEmpty() && activityDesc.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty() && activityLink.isNotEmpty() -> true
            else ->{
                Toast.makeText(requireContext(), "Enter each and every field", Toast.LENGTH_SHORT).show()
                backToBaseFragment()
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
        cal.set(Calendar.DAY_OF_MONTH, day)
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