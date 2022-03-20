package com.example.chatme.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatme.R
import com.example.chatme.data.model.Groups
import kotlinx.android.synthetic.main.group_layout.view.*
import kotlinx.android.synthetic.main.user_layout.view.*
import kotlinx.android.synthetic.main.user_layout.view.userNameAdap

class GroupAdapter(val data: ArrayList<Groups>, val onClick: OnClickItem) :
    RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.group_layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val group = data[position]

        holder.itemView.apply {
            groupName.setText(group.name)
            groupUsersNumber.setText(group.userId.size.toString() + " Users")

            setOnClickListener {
                onClick.onClick(group)
            }
        }
    }

    override fun getItemCount() = data.size


    interface OnClickItem {
        fun onClick(group: Groups)
    }

}

