package com.example.chatme.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Groups(
    var id : String = "",
    var name : String = "",
    var userId : ArrayList<String>
) : Parcelable
