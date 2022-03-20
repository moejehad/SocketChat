package com.example.chatme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.data.model.Users
import kotlinx.android.synthetic.main.user_layout.view.*

class UserAdapter(val data: ArrayList<Users> , val onClick:OnClickItem) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.user_layout,parent,false
        ))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = data[position]

        holder.itemView.apply {
            userNameAdap.setText(user.name)
            userLetter.setText(user.name.toString().substring(0,1).uppercase())
            setOnClickListener{
                onClick.onClick(user)
            }
            onLineStatus.setBackgroundResource(
                if (user.isOnline)
                    R.drawable.online_shap
                else
                    R.drawable.letter_shap
            )
        }
    }

    override fun getItemCount() = data.size


    interface OnClickItem {
        fun onClick(user : Users)
    }

}