package com.example.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bookapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }


        binding.privacy.setOnClickListener {

            startActivity(Intent(this@AboutActivity,PrivacyPolicyActivity::class.java))

        }
        binding.aboutUs.setOnClickListener {

            startActivity(Intent(this@AboutActivity,AboutUsActivity::class.java))

        }
    }

}