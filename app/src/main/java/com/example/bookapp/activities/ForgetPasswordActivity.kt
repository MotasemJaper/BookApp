package com.example.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityForgetPasswordBinding

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnForgetPass.setOnClickListener {
            startActivity(Intent(this@ForgetPasswordActivity,VerifyOTPActivity::class.java))
        }
    }
}