package com.example.chatme.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Users(
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
    var isOnline: Boolean = false
) : Parcelable
