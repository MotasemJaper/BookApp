package com.example.bookapp.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.databinding.ActivityEditCourseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCourseBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var majorArrayList: ArrayList<ModelMajor>
    private lateinit var collegeArrayList: ArrayList<ModelCollege>

    private var selectedMajorId = ""
    private var selectedMajorTitle = ""
    private var selectedCollegeTitle = ""
    private var selectedCollegeId = ""
    private var level =  ""
    var id = ""
    var timestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        collegeArrayList = ArrayList()
        majorArrayList = ArrayList()
        loadColleges()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        val intent = intent
        val majorName = intent.getStringExtra("majorName")!!
        val level = intent.getStringExtra("level")!!
        id = intent.getStringExtra("id")!!

        Log.d("majorName" ,"onCreate: $level")

        val ref = FirebaseDatabase.getInstance().getReference("Courses").child(majorName).child(level).child(id)
        ref.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.addCourseName.setText(snapshot.child("courseName").value.toString())

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddCourse.setOnClickListener {
            validateData()
        }
        binding.ViewMajorName.setOnClickListener {
            majorPickDialog()
        }
        binding.ViewCollegeName.setOnClickListener {
            collegePickDialog()
        }

        binding.level.setOnClickListener {
            selectTypeMaterial()
        }



    }

    private fun validateData() {


        if (!validAll()){
            Toast.makeText(this@EditCourseActivity, "Please All Required !!", Toast.LENGTH_SHORT).show()
        } else {
            AddCourse()
            //AddCourseForTowRelation()
        }

    }

    private fun selectTypeMaterial() {
        val view: View = LayoutInflater.from(this)
            .inflate(com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)

        alertDialogBuilder.setTitle("Select Level  ..")
        alertDialogBuilder.setCancelable(true)
        val type = arrayOf("1","2","3","4","5")
        alertDialogBuilder.setItems(type) { dialog, which ->
            level = type[which].toString()
            binding.level.text = level
            Log.d("slides", "selectTypeMaterial: $level")
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun AddCourse() {
        progressDialog.setMessage("Adding Course info")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["majorId"] = "$selectedMajorId"
        hashMap["majorName"] = "$selectedMajorTitle"
        hashMap["courseName"] = "${binding.addCourseName.text.toString().trim()}"
        hashMap["level"] = "$level"
        hashMap["timestamp"] = "$timestamp"

        val reff = FirebaseDatabase.getInstance().getReference("CoursesForRelations")
            .child(selectedMajorTitle).child(id)
        reff.updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.addCourseName.text.clear()

            }//////////////////////
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@EditCourseActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        val ref = FirebaseDatabase.getInstance().getReference("Courses").child(selectedMajorTitle)
            .child("$level").child(id)
        ref.updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@EditCourseActivity, "Added...", Toast.LENGTH_SHORT).show()
                binding.addCourseName.text.clear()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@EditCourseActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        val hashNoti = HashMap<String,Any>()
        hashNoti["nameCollege"] = binding.addCourseName.text.toString().trim()+" "+"has been added to the Course Go check.."
        hashNoti["id"] = "$selectedMajorId"
        hashNoti["name"] = "${binding.addCourseName.text.toString().trim()}"
        val refNotification = FirebaseDatabase.getInstance().getReference("Notifications")
        refNotification.child("$timestamp").updateChildren(hashNoti)
    }
    private fun AddCourseForTowRelation() {
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["level"] = "$level"
        hashMap["majorId"] = "$selectedMajorId"
        hashMap["courseName"] = "${binding.addCourseName.text.toString().trim()}"
        hashMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("CoursesForRelations")
            .child(selectedMajorTitle).child(id)
        ref.updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.addCourseName.text.clear()

            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@EditCourseActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

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


            }
            .show()
    }

    fun validAll(): Boolean {
        return isEmty(binding.ViewCollegeName, "Required") and
                isEmty(binding.ViewMajorName, "Required") and
                isEmty(binding.addCourseName, "Required") and
                isEmty(binding.level, "Required")

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