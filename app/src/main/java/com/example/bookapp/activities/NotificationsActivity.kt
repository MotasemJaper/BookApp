package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.activities.Model.NotificationModel
import com.example.bookapp.activities.adapter.AdapterNotification
import com.example.bookapp.activities.adapter.AdapterViewMajors
import com.example.bookapp.databinding.ActivityNotificationsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notiList: ArrayList<NotificationModel>
    private lateinit var adapterNoti: AdapterNotification
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notiList = ArrayList()
        notiList.reverse()
        loadNotifications()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadNotifications(){
        val ref = FirebaseDatabase.getInstance().getReference("Notifications")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notiList.clear()
                for (ds in snapshot.children){
                    val noti = ds.getValue(NotificationModel::class.java)
                    notiList.add(noti!!)
                    adapterNoti = AdapterNotification(this@NotificationsActivity,notiList)
                    val manger = LinearLayoutManager(this@NotificationsActivity)
                    binding.recyclerNotifications.adapter = adapterNoti
                    binding.recyclerNotifications.layoutManager = manger
                    adapterNoti.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

}