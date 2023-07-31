package com.example.bookapp.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.Model.ModelLevel
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var courseArrayList: ArrayList<ModelCourse>
    private lateinit var majorArrayList: ArrayList<ModelMajor>
    private lateinit var collegeArrayList: ArrayList<ModelCollege>
    private lateinit var levelList: ArrayList<ModelLevel>

    private var selectedMajorId = ""
    private var selectedMajorTitle = ""
    private var selectedCollegeTitle = ""
    private var selectedCollegeId = ""
    private var selectedCourseId = ""
    private var selectedCourseTitle = ""
    private var selectedLevel = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        collegeArrayList = ArrayList()
        majorArrayList = ArrayList()
        courseArrayList = ArrayList()
        levelList = ArrayList()
        loadColleges()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddMajor.setOnClickListener {
            validateData()
        }
        binding.ViewCourseName.setOnClickListener {
            coursePickDialog()
        }
        binding.ViewCollegeName.setOnClickListener {
            collegePickDialog()
        }
        binding.ViewMajorName.setOnClickListener {
            majorPickDialog()
        }

//        binding.ViewLevel.setOnClickListener {
//            levelPickDialog()
//        }

    }

    private fun validateData() {

        if (!validAll()) {
            Toast.makeText(this@AddBookActivity, "Please All Required !!", Toast.LENGTH_SHORT).show()
        } else {
            AddBooks()
            AddBooksForTowRelations()

        }

    }

    private fun AddBooks() {
        progressDialog.setMessage("Adding Books info")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["courseId"] = "$selectedCourseId"
        hashMap["bookName"] = "${binding.addBookName.text.toString().trim()}"
        hashMap["bookDescription"] = "${binding.BookDescription.text.toString().trim()}"
        hashMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("Books").child(selectedCourseTitle)
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@AddBookActivity, "Added...", Toast.LENGTH_SHORT).show()
                binding.addBookName.text.clear()
                binding.BookDescription.text.clear()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@AddBookActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun AddBooksForTowRelations() {
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["courseId"] = "$selectedCourseId"
        hashMap["bookName"] = "${binding.addBookName.text.toString().trim()}"
        hashMap["bookDescription"] = "${binding.BookDescription.text.toString().trim()}"
        hashMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("BooksForTowRelations")
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@AddBookActivity, "Added...", Toast.LENGTH_SHORT).show()
                binding.addBookName.text.clear()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@AddBookActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun loadLevel(majorName: String){
        val ref = FirebaseDatabase.getInstance().getReference("CoursesForRelations")
            .child(majorName)
            .orderByChild("level")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                levelList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelLevel::class.java)
                    Log.d("levelName", "onDataChange: ${model!!.level}")
                    levelList.add(model!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun loadColleges() {
        val ref = FirebaseDatabase.getInstance().getReference("Colleges")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                collegeArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCollege::class.java)
                    collegeArrayList.add(model!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadMajors(selectedCollegeTitle: String) {
        Log.d("selectedCollegeTitle", "collegePickDialog: $selectedCollegeTitle")
        val ref = FirebaseDatabase.getInstance().getReference("Majors").child(selectedCollegeTitle)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                majorArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelMajor::class.java)
                    Log.d("majorName", "onDataChange: ${snapshot.child("majorName").value}")
                    majorArrayList.add(model!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun collegePickDialog() {

        val collegesArray = arrayOfNulls<String>(collegeArrayList.size)
        for (i in collegeArrayList.indices) {
            collegesArray[i] = collegeArrayList[i].college
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick College")
            .setItems(collegesArray) { dialog, which ->
                selectedCollegeTitle = collegeArrayList[which].college
                selectedCollegeId = collegeArrayList[which].id
                binding.ViewCollegeName.text = selectedCollegeTitle
                loadMajors(selectedCollegeTitle)
            }
            .show()

    }

    private fun levelPickDialog(){

        val level = arrayOfNulls<String>(levelList.size)
        for (i in levelList.indices) {
            level[i] = levelList[i].level
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Level")
            .setItems(level) { dialog, which ->

                selectedLevel = levelList[which].level
               // binding.ViewLevel.text = selectedLevel
                Log.d("mm", "majorPickDialog: $selectedLevel")

            }
            .show()
    }

    private fun majorPickDialog() {

        val majorArray = arrayOfNulls<String>(majorArrayList.size)
        for (i in majorArrayList.indices) {
            majorArray[i] = majorArrayList[i].majorName
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Major")
            .setItems(majorArray) { dialog, which ->

                selectedMajorTitle = majorArrayList[which].majorName
                selectedMajorId = majorArrayList[which].id
                binding.ViewMajorName.text = selectedMajorTitle
                Log.d("asdasdasdas", "majorPickDialog: $selectedMajorTitle")
                loadCourses(selectedMajorTitle)
                 //loadLevel(selectedMajorTitle)

            }
            .show()
    }

    private fun loadCourses(selectedMajorTitle: String) {

        val ref = FirebaseDatabase.getInstance().getReference("CoursesForRelations")
            .child(selectedMajorTitle)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCourse::class.java)
                    Log.d("asjdhjkasda", "onDataChange: ${model!!.majorName}")
                    courseArrayList.add(model!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun coursePickDialog() {

        val majorArray = arrayOfNulls<String>(courseArrayList.size)
        for (i in courseArrayList.indices) {
            majorArray[i] = courseArrayList[i].courseName
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Course")
            .setItems(majorArray) { dialog, which ->

                selectedCourseTitle = courseArrayList[which].courseName
                selectedCourseId = courseArrayList[which].id
                binding.ViewCourseName.text = selectedCourseTitle


            }
            .show()
    }

    fun validAll(): Boolean {
        return isEmty(binding.ViewCollegeName, "Required") and
                isEmty(binding.ViewMajorName, "Required") and
                isEmty(binding.ViewCourseName, "Required") and
                isEmty(binding.addBookName, "Required") and
                isEmty(binding.BookDescription, "Required")
    }

    fun isEmty(editText: TextView?, msg: String?): Boolean {
        var isDone = true
        if (editText != null) {
            if (editText.text.toString().isEmpty()) {
                editText.error = msg
                isDone = false
            }
        }
        return isDone
    }
}