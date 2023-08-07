package com.example.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.activities.adapter.AdapterViewMajors
import com.example.bookapp.databinding.ActivityMajorsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MajorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMajorsBinding
    private lateinit var majorList: ArrayList<ModelMajor>
    private lateinit var adapterMajor: AdapterViewMajors
    private var collegeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMajorsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        majorList = ArrayList()
        val intent = intent
        collegeId = intent.getStringExtra("college")!!
        Log.d("intenrID", "onCreate: $collegeId")
        loadMajors()
        binding.searchCourse.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterMajor.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadMajors(){
        val ref = FirebaseDatabase.getInstance().getReference("Majors").child(collegeId)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                majorList.clear()
                for (ds in snapshot.children){
                    val major = ds.getValue(ModelMajor::class.java)
                    Log.d("majorName", "onDataChange: ${major!!.majorName}")
                    majorList.add(major!!)
                    adapterMajor = AdapterViewMajors(this@MajorsActivity,majorList)
                    val manger = GridLayoutManager(this@MajorsActivity,2)
                    binding.majorRecycler.adapter = adapterMajor
                    binding.majorRecycler.layoutManager = manger
                    adapterMajor.notifyDataSetChanged()
                }

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}