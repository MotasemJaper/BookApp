package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.UserDashBord
import com.example.bookapp.activities.adapter.AdapterViewRequest
import com.example.bookapp.databinding.ActivityViewRequestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewRequestBinding
    private lateinit var list: ArrayList<UserDashBord>
    private lateinit var adapter: AdapterViewRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = ArrayList()

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

        loadAllRequests()

    }

    private fun loadAllRequests(){
        val ref = FirebaseDatabase.getInstance().getReference("RequestPermission")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(UserDashBord::class.java)
                    list.add(model!!)
                    adapter = AdapterViewRequest(this@ViewRequestActivity,list)
                    val manger = LinearLayoutManager(this@ViewRequestActivity)
                    binding.recyclerDashBoard.adapter = adapter
                    binding.recyclerDashBoard.layoutManager = manger
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
}