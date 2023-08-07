package com.example.bookapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed(
            Runnable {
                checkUser()
            },
            1000,
        )
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        } else {
            val firebaseUser = firebaseAuth.currentUser!!
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userType = snapshot.child("userType").value
                        if (userType == "user") {
                            startActivity(Intent(this@SplashActivity, DashboredUserActivity::class.java))
                            finish()
                        } else if (userType == "admin") {
                            startActivity(Intent(this@SplashActivity, DashboredAdminActivity::class.java))
                            finish()
                        }else{
                            startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SplashActivity, "${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
