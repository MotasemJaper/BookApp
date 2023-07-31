package com.example.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.UserDashBord
import com.example.bookapp.activities.adapter.AdapterDashBord
import com.example.bookapp.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var list: ArrayList<UserDashBord>
    private lateinit var adapter: AdapterDashBord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        list = ArrayList()
        loadUserInfo()

        binding.logout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.btnREq.setOnClickListener {
            startActivity(Intent(this@Dashboard,ViewRequestActivity::class.java))
        }

        binding.search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            mAuth.signOut()
            val intent = Intent(this@Dashboard,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun loadUserInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(UserDashBord::class.java)
                        list.add(model!!)
                        adapter = AdapterDashBord(this@Dashboard,list)
                        binding.recyclerDashBoard.adapter = adapter
                        val manger = LinearLayoutManager(this@Dashboard)
                        binding.recyclerDashBoard.layoutManager = manger
                        adapter.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


}