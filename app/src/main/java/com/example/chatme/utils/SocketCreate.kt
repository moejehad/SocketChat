package com.example.chatme.utils

import android.content.Context
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class SocketCreate private constructor(private var context: Context) {

    private var socket: Socket? = null
    private val BASE_URL = "http://10.10.10.237:6000"

    companion object {
        var instance: SocketCreate? = null
        fun getInstance(context: Context): SocketCreate? {
            if (instance == null) {
                instance = SocketCreate(context)
            }
            return instance
        }
    }

    fun getSocket() = socket

    init {
        socket = IO.socket(BASE_URL)
        socket!!.connect()
    }


}