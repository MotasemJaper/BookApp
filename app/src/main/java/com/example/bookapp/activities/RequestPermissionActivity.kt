package com.example.bookapp.activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.databinding.ActivityRequestPermissionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestPermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestPermissionBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnRequest.setOnClickListener {
            if (!validAll()){
                Toast.makeText(this@RequestPermissionActivity, "Please All Required !!", Toast.LENGTH_SHORT).show()
            }else{
                sendRequest()
                binding.fullNameReq.text.clear()
                binding.unID.text.clear()
                binding.gpa.text.clear()
            }
        }
    }

    private fun sendRequest() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hasMap: HashMap<String, Any> = HashMap()
        hasMap["name"] = binding.fullNameReq.text.toString().trim()
        hasMap["unId"] = binding.unID.text.toString().trim()
        hasMap["gpa"] = binding.gpa.text.toString().trim()
        hasMap["timestamp"] = "$timestamp"
        hasMap["id"] = "$timestamp"
        hasMap["Uid"] = "${firebaseAuth.uid}"
        val ref = FirebaseDatabase.getInstance().getReference("RequestPermission")
        ref.child("$timestamp").setValue(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(
                    this@RequestPermissionActivity,
                    "Thank You For Send Request !!",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@RequestPermissionActivity, "${e.message}", Toast.LENGTH_SHORT).show()
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

    fun validAll(): Boolean {
        return isEmty(binding.fullNameReq, "Required") and
                isEmty(binding.unID, "Required") and
                isEmty(binding.gpa, "Required")
    }

    fun isEmty(editText: TextView?, msg: String?): Boolean {
        var isDone = true
        if (editText != null) {
            if (editText.text.toString().isEmpty()) {
                editText.error = msg
                isDone = false
            }
        }
        return isDone
    }

}