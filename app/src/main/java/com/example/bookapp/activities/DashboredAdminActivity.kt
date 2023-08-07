package com.example.bookapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.bookapp.R
import com.example.bookapp.activities.Fragment.CollegeFragment
import com.example.bookapp.activities.Fragment.FavFragment
import com.example.bookapp.activities.Fragment.HomeAdminFragment
import com.example.bookapp.activities.Fragment.ProfileFragment
import com.example.bookapp.activities.Notifications.Token
//import com.example.bookapp.adapters.AdapterCategory
import com.example.bookapp.databinding.ActivityDashboredAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
//import com.example.bookapp.models.ModelCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging

class DashboredAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboredAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var categoryArrayList: ArrayList<ModelCategory>
//    private lateinit var adapterCategory: AdapterCategory

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_dashbored_admin)

        binding = ActivityDashboredAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        supportFragmentManager.beginTransaction().replace(R.id.frameAdmin,HomeAdminFragment()).commit()
        binding.buttonNavAdmin.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment
            when (menuItem.itemId) {
                R.id.home_user -> {
                   binding.titleTv.text = "Home"
                    fragment =  HomeAdminFragment()
                    openFragment(fragment)
                    binding.btnEditProfile.visibility = View.GONE
                    true
                }
                R.id.college_user -> {
                    binding.titleTv.text = "College"
                    fragment =  CollegeFragment()
                    openFragment(fragment)
                    binding.btnEditProfile.visibility = View.GONE
                    true
                }
                R.id.fav_user -> {
                    binding.titleTv.text = "Favorite"
                    fragment =  FavFragment()
                    openFragment(fragment)
                    binding.btnEditProfile.visibility = View.GONE
                    true
                }

                R.id.profile_user -> {
                    binding.titleTv.text = "Profile"
                    fragment =  ProfileFragment()
                    openFragment(fragment)
                    binding.btnEditProfile.visibility = View.VISIBLE
                    binding.btnEditProfile.setOnClickListener {
                        startActivity(Intent(this@DashboredAdminActivity,EditProfileActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }

        binding.questios.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,AllMessageActivityForAdmin::class.java))
        }

        binding.notiBtn.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,NotificationsActivity::class.java))
        }


        binding.addCollegeFab.setClosedOnTouchOutside(true)
        binding.fabAddCollege.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,AddCollegeActivity::class.java))
        }
        binding.fabAddMajor.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,AddMajorActivity::class.java))
        }
        binding.fabAddCourse.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,AddCourseActivity::class.java))
        }
//        binding.fabAddBook.setOnClickListener {
//            startActivity(Intent(this@DashboredAdminActivity,AddBookActivity::class.java))
//        }
        binding.fabAddMaterial.setOnClickListener {
            startActivity(Intent(this@DashboredAdminActivity,AddMaterialActivity::class.java))
        }



        updateToken(FirebaseMessaging.getInstance().token.toString())

    }

    private fun updateToken(token: String){
        val ref = FirebaseDatabase.getInstance().getReference("Tokens")
        val t = Token(token)
        ref.child(firebaseAuth.uid!!).setValue(t)
    }


    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
          //  startActivity(Intent(this@DashboredAdminActivity, MainActivity::class.java))
           // finish()
        } else {
            val email = firebaseUser.email
          //  binding.subTitleTv.text = email
        }
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameAdmin,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
