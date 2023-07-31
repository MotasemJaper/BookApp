package com.example.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityVerifyOtpactivityBinding

class VerifyOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.verifyBtn.setOnClickListener {
            startActivity(Intent(this@VerifyOTPActivity,LoginActivity::class.java))
        }
    }
}