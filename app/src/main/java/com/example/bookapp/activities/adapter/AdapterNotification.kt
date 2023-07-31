package com.example.bookapp.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.activities.Model.NotificationModel
import com.example.bookapp.databinding.ItemForMajorBinding
import com.example.bookapp.databinding.ListNotificationsBinding

class AdapterNotification : RecyclerView.Adapter<AdapterNotification.ViewHolder>{

    private lateinit var binding: ListNotificationsBinding
    private val context: Context
    public var modelNoti: ArrayList<NotificationModel>

    constructor(context: Context, modelNoti: ArrayList<NotificationModel>) {
        this.context = context
        this.modelNoti = modelNoti
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterNotification.ViewHolder {
        binding = ListNotificationsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterNotification.ViewHolder, position: Int) {
        val noti = modelNoti[position]
        val nameCollege = noti.nameCollege
        val name = noti.name
        val id = noti.id
        holder.nameCollege.text = nameCollege
        holder.nameCollegeAdded.text = name + " Added"
    }

    override fun getItemCount(): Int {
        return modelNoti.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameCollege: TextView = binding.nameCollege
        var nameCollegeAdded: TextView = binding.nameCollegeAdded


    }
}