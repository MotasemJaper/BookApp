package com.example.bookapp.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityEditMajorBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditMajorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditMajorBinding
    var id :String = ""
    var collegeName :String = ""
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityEditMajorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        val intent = intent
        collegeName = intent.getStringExtra("collegeId")!!
        id = intent.getStringExtra("id")!!

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        loadMajorName()

        binding.update.setOnClickListener {
            updateMajorName()
        }
    }

    private fun loadMajorName(){
        val ref = FirebaseDatabase.getInstance().getReference("Majors").child(collegeName).child(id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val college = snapshot.child("majorName").value.toString()
                binding.addMajorName.setText(college)

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }
    private fun updateMajorName(){
        progressDialog.show()
        val hasMap = HashMap<String,Any?>()
        hasMap["majorName"] = "${binding.addMajorName.text.trim()}"
        val ref = FirebaseDatabase.getInstance().getReference("Majors").child(collegeName).child(id)
        ref.updateChildren(hasMap).addOnCompleteListener { task->
            progressDialog.dismiss()
            Toast.makeText(this@EditMajorActivity, "Updated", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e->
                progressDialog.dismiss()
            }
    }
}