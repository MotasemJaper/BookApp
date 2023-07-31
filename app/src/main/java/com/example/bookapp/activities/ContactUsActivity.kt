package com.example.bookapp.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.databinding.ActivityContactUsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactUsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnForSend.setOnClickListener {
            if (binding.emailUserForContact.text.isEmpty() &&
                binding.messgae.text.isEmpty()
            ) {

                Toast.makeText(
                    this@ContactUsActivity,
                    "Please Enter your Message !!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadMessage()
                binding.emailUserForContact.text.clear()
                binding.messgae.text.clear()
            }

        }
    }

    private fun uploadMessage() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hasMap: HashMap<String, Any> = HashMap()
        hasMap["name"] = binding.nameUserForContact.text.toString().trim()
        hasMap["email"] = binding.emailUserForContact.text.toString().trim()
        hasMap["message"] = binding.messgae.text.toString().trim()
        hasMap["timestamp"] = timestamp
        hasMap["id"] = timestamp
        hasMap["Uid"] = "${firebaseAuth.uid}"
        val ref = FirebaseDatabase.getInstance().getReference("ContactUs")
        ref.child("$timestamp").setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@ContactUsActivity,
                    "Thank You For Send Message !!",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@ContactUsActivity, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"
                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}