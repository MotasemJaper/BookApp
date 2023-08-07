package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.activities.Model.Message
import com.example.bookapp.activities.adapter.AdapterForAllMessage
import com.example.bookapp.databinding.ActivityAllMessageForAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllMessageActivityForAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityAllMessageForAdminBinding
    private lateinit var list: ArrayList<Message>
    private lateinit var adapter: AdapterForAllMessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllMessageForAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = ArrayList()

        loadAllContactUs()


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }

    private fun loadAllContactUs() {
        val ref = FirebaseDatabase.getInstance().getReference("ContactUs")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(Message::class.java)
                    list.add(model!!)
                    adapter = AdapterForAllMessage(this@AllMessageActivityForAdmin,list)
                    val manger = LinearLayoutManager(this@AllMessageActivityForAdmin)
                    binding.recyclerAllMessage.adapter = adapter
                    binding.recyclerAllMessage.layoutManager = manger
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
}