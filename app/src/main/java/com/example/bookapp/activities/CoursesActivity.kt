package com.example.bookapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.bookapp.activities.Fragment.BooksUserFragment
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.adapter.AdapterViewCourse
import com.example.bookapp.databinding.ActivityCoursesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CoursesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoursesBinding
    private lateinit var courseList: ArrayList<ModelCourse>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var courseArrayList: ArrayList<ModelCourse>
    private var majorIdForMajorName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        courseList = ArrayList()
        val intent = intent
        majorIdForMajorName = intent.getStringExtra("majorName")!!
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        setUpWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun setUpWithViewPagerAdapter(viewPager: ViewPager){

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this)
        courseArrayList = ArrayList()

        val re = FirebaseDatabase.getInstance().getReference("Courses").child(majorIdForMajorName)
        re.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                courseArrayList.clear()

                val levelOne = ModelCourse("01","Level 1","","","1","","")
                val levelTow = ModelCourse("01","Level 2","","","1","","")
                val levelThree = ModelCourse("01","Level 3","","","1","","")
                val levelFour = ModelCourse("01","Level 4","","","1","","")
                val levelFive = ModelCourse("01","Level 5","","","1","","")


                courseArrayList.add(levelOne)
                courseArrayList.add(levelTow)
                courseArrayList.add(levelThree)
                courseArrayList.add(levelFour)
                courseArrayList.add(levelFive)

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${levelOne.id}",
                        "${levelOne.level}",
                        "${levelOne.courseName}",
                        "${levelOne.majorId}",
                        "${levelOne.timestamp}",
                        "$majorIdForMajorName",
                        "${levelOne.uid}"
                    ),"Level 1"
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${levelTow.id}",
                        "${levelTow.level}",
                        "${levelTow.courseName}",
                        "${levelTow.majorId}",
                        "${levelTow.timestamp}",
                        "$majorIdForMajorName",
                        "${levelTow.uid}"
                    ),"Level 2"
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${levelThree.id}",
                        "${levelThree.level}",
                        "${levelThree.courseName}",
                        "${levelThree.majorId}",
                        "${levelThree.timestamp}",
                        "$majorIdForMajorName",
                        "${levelThree.uid}"
                    ),"Level 3"
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${levelFour.id}",
                        "${levelFour.level}",
                        "${levelFour.courseName}",
                        "${levelFour.majorId}",
                        "${levelFour.timestamp}",
                        "$majorIdForMajorName",
                        "${levelFour.uid}"
                    ),"Level 4"
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        "${levelFive.id}",
                        "${levelFive.level}",
                        "${levelFive.courseName}",
                        "${levelFive.majorId}",
                        "${levelFive.timestamp}",
                        "$majorIdForMajorName",
                        "${levelFive.uid}"
                    ),"Level 5"
                )

                viewPagerAdapter.notifyDataSetChanged()

                for (ds in snapshot.children){
                    Log.d("skdsddssd", "onDataChange: ${ds.child("courseName").value}")
                    val model = ds.getValue(ModelCourse::class.java)
                    Log.d("NameCourse", "onDataChange: "+model!!.courseName)
                    courseArrayList.add(model!!)
                    viewPagerAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context): FragmentPagerAdapter(fm,behavior){
        private val fragmentsList: ArrayList<BooksUserFragment> = ArrayList()
        private val fragmentTitleList: ArrayList<String> = ArrayList()
        private val context: Context

        init {
            this.context = context
        }

        override fun getCount(): Int {

            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {

            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        public fun addFragment(fragment: BooksUserFragment, title: String){

            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }

    }

}