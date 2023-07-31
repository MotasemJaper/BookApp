package com.example.bookapp.activities.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.bookapp.R
import com.example.bookapp.activities.AboutActivity
import com.example.bookapp.activities.ContactUsActivity
import com.example.bookapp.activities.EditProfileActivity
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.LoginActivity
import com.example.bookapp.activities.RequestPermissionActivity
import com.example.bookapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            activity?.finish()
        }

        binding.aboutProfile.setOnClickListener {
            startActivity(Intent(context,AboutActivity::class.java))
        }
        binding.contactUs.setOnClickListener {
            startActivity(Intent(context,ContactUsActivity::class.java))
        }
        binding.requestPer.setOnClickListener {
            startActivity(Intent(context,RequestPermissionActivity::class.java))
        }
        return binding.root

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

                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.accountTypeTv.text = userType

                    try {
                        Glide.with(this@ProfileFragment)
                            .load(profileImage)
                            .placeholder(R.drawable.profilepic)
                            .into(binding.profileIv)
                    } catch (e: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}