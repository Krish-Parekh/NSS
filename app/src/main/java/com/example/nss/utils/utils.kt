package com.example.nss.utils

import android.app.Dialog
import android.content.Context
import com.example.nss.R


private lateinit var dialog: Dialog
fun getDialogBox(context: Context){
    dialog = Dialog(context)
    dialog.setContentView(R.layout.loading_dialog)
    dialog.setCancelable(false)
    dialog.show()
}
fun dismissDialogBox(){
    dialog.dismiss()
}