package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }
}