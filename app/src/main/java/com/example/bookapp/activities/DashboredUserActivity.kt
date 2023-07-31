package com.example.bookapp.activities

// import android.content.Intent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.bookapp.R
import com.example.bookapp.activities.Fragment.CollegeFragment
import com.example.bookapp.activities.Fragment.FavFragment
import com.example.bookapp.activities.Fragment.HomeAdminFragment
import com.example.bookapp.activities.Fragment.ProfileFragment
// import com.example.bookapp.BooksUserFragment
import com.example.bookapp.databinding.ActivityDashboredUserBinding
// import com.example.bookapp.models.ModelCategory
import com.google.firebase.auth.FirebaseAuth

class DashboredUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboredUserBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityDashboredUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.notiBtn.setOnClickListener {
            startActivity(Intent(this@DashboredUserActivity,NotificationsActivity::class.java))
        }

        supportFragmentManager.beginTransaction().replace(R.id.frameAdmin, HomeAdminFragment()).commit()
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
                        startActivity(Intent(this@DashboredUserActivity,EditProfileActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameAdmin,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
