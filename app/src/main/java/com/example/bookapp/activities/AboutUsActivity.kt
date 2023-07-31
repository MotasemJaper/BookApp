package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.R
import com.example.bookapp.databinding.ActivityAboutBinding
import com.example.bookapp.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
}