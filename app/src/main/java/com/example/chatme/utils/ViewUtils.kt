package com.example.chatme.utils

import android.content.Context
import android.widget.Toast

fun Context.toastMsg(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}
