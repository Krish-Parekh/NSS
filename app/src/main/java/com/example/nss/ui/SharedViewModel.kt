package com.example.nss.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val core = MutableLiveData<Boolean>()
    fun sendMessage(corePass: Boolean) {
        core.value = corePass
    }
}