package com.example.bookapp.activities.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.adapter.AdapterViewCourse
import com.example.bookapp.databinding.FragmentBooksUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BooksUserFragment : Fragment {


    private lateinit var binding: FragmentBooksUserBinding

    public companion object {

        private const val TAG = "BOOKS_USER_TAG"

        public fun newInstance(
            id: String,
            level: String,
            courseName: String,
            majorId: String,
            timestamp: String,
            majorName: String,
            uid: String

        ): BooksUserFragment {

            val fragment = BooksUserFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("level", level)
            args.putString("courseName", courseName)
            args.putString("majorId", majorId)
            args.putString("majorName", majorName)
            args.putString("timestamp", timestamp)
            args.putString("uid", uid)

            args.putString("level1",level)
            args.putString("level2",level)
            args.putString("level3",level)
            args.putString("level4",level)
            args.putString("level5",level)
            fragment.arguments = args
            return fragment
        }
    }

    private var id = ""
    private var level = ""
    private var courseName = ""
    private var majorName = ""
    private var majorId = ""
    private var timestamp = ""
    private var uid = ""

    private var level1 = ""
    private var level2 = ""
    private var level3 = ""
    private var level4 = ""
    private var level5 = ""

    private lateinit var courseList: ArrayList<ModelCourse>
    private lateinit var adapterCourse: AdapterViewCourse

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        if (args != null) {

            id = args.getString("id")!!
            level = args.getString("level")!!
            courseName = args.getString("courseName")!!
            majorId = args.getString("majorId")!!
            majorName = args.getString("majorName")!!
            timestamp = args.getString("timestamp")!!
            uid = args.getString("uid")!!


            level1 = args.getString("level1")!!
            level2 = args.getString("level2")!!
            level3 = args.getString("level3")!!
            level4 = args.getString("level4")!!
            level5 = args.getString("level5")!!


        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context), container, false)

//        if (level1 == "Level 1"){
//            loadLevelOne(majorName)
//        }else if (level2 == "Level 2"){
//            loadLevelTow(majorName)
//        }else if (level3 == "Level 3"){
//            loadLevelThree(majorName)
//        }else if (level4 == "Level 4"){
//            loadLevelFour(majorName)
//        }else if(level5 == "Level 5"){
//            loadLevelFive(majorName)
//        }

        when (level) {
            "Level 1" -> {

                loadLevelOne(majorName)

            }
            "Level 2" -> {

                loadLevelTow(majorName)

            }
            "Level 3" -> {

                loadLevelThree(majorName)

            }
            "Level 4" -> {

                loadLevelFour(majorName)

            }

            "Level 5" -> {


                loadLevelFive(majorName)

            }
        }

        binding.searchCourse.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {

                    adapterCourse.filter.filter(s)

                }catch (e: Exception){
                    Log.d(TAG, "onTextChanged: Search Exception ${e.message}")

                }

            }

            override fun afterTextChanged(s: Editable?) {


            }
        })

        return binding.root
    }

   private fun loadLevelOne(majorName: String){
       Log.d("nameMajor", "loadLevelOne: $majorName")
       courseList  = ArrayList()
       val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child("1")
           .addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   courseList.clear()
                   for (ds in snapshot.children){
                       val model = ds.getValue(ModelCourse::class.java)
                       Log.d("alksjdlakjsdlkas", "onDataChange: ${model!!.courseName}")
                       courseList.add(model!!)
                   }

                   adapterCourse = AdapterViewCourse(requireContext(),courseList)
                   binding.courseRecycler.adapter = adapterCourse
                   val manger = GridLayoutManager(context,2)
                   binding.courseRecycler.layoutManager = manger
                   adapterCourse.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {



               }
           })
    }
   private fun loadLevelTow(majorName: String){
       courseList  = ArrayList()
       val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child("2")
           .addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {

                   courseList.clear()
                   for (ds in snapshot.children){
                       val model = ds.getValue(ModelCourse::class.java)
                       courseList.add(model!!)
                   }

                   adapterCourse = AdapterViewCourse(context!!,courseList)
                   binding.courseRecycler.adapter = adapterCourse
                   val manger = GridLayoutManager(context,2)
                   binding.courseRecycler.layoutManager = manger
                   adapterCourse.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {



               }
           })
    }
   private fun loadLevelThree(majorName: String){
       courseList  = ArrayList()
       val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child("3")
           .addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {

                   courseList.clear()
                   for (ds in snapshot.children){
                       val model = ds.getValue(ModelCourse::class.java)
                       courseList.add(model!!)
                   }

                   adapterCourse = AdapterViewCourse(context!!,courseList)
                   binding.courseRecycler.adapter = adapterCourse
                   val manger = GridLayoutManager(context,2)
                   binding.courseRecycler.layoutManager = manger
                   adapterCourse.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {



               }
           })
    }
   private fun loadLevelFour(majorName: String){
       courseList  = ArrayList()
       val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child("4")
           .addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {

                   courseList.clear()
                   for (ds in snapshot.children){
                       val model = ds.getValue(ModelCourse::class.java)
                       courseList.add(model!!)
                   }

                   adapterCourse = AdapterViewCourse(context!!,courseList)
                   binding.courseRecycler.adapter = adapterCourse
                   val manger = GridLayoutManager(context,2)
                   binding.courseRecycler.layoutManager = manger
                   adapterCourse.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {



               }
           })
    }
   private fun loadLevelFive(majorName: String){
       courseList  = ArrayList()
       val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child("5")
           .addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {

                   courseList.clear()
                   for (ds in snapshot.children){
                       val model = ds.getValue(ModelCourse::class.java)
                       courseList.add(model!!)
                   }

                   adapterCourse = AdapterViewCourse(context!!,courseList)
                   binding.courseRecycler.adapter = adapterCourse
                   val manger = GridLayoutManager(context,2)
                   binding.courseRecycler.layoutManager = manger
                   adapterCourse.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {



               }
           })
    }



}