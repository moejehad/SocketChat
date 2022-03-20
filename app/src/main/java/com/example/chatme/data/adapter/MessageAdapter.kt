package com.example.chatme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.data.model.Message
import com.example.chatme.ui.login.LoginFragment
import kotlinx.android.synthetic.main.item_sender.view.*

class MessageAdapter(val dataMessage: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SenderViewHolder(item: View) : RecyclerView.ViewHolder(item.rootView){
        fun bind(message: Message){
            itemView.rootView.txtMsg.setText(message.message)
        }
    }

    inner class ReceverViewHolder(item: View) : RecyclerView.ViewHolder(item.rootView){
        fun bind(message: Message){
            itemView.rootView.txtMsg.setText(message.message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> {
                SenderViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_sender,parent,false)
                )
            }
            else -> {
                ReceverViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_reciver,parent,false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SenderViewHolder){
            holder.bind(dataMessage[position])
        }else if (holder is ReceverViewHolder){
            holder.bind(dataMessage[position])
        }
    }

    override fun getItemCount(): Int = dataMessage.size

    override fun getItemViewType(position: Int): Int {
        val message = dataMessage[position]

        return when(message.id){
            LoginFragment.users.id -> {
                0
            }else -> {
                1
            }
        }
    }
}