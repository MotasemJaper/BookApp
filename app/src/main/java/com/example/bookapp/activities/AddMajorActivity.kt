package com.example.bookapp.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.databinding.ActivityAddMajorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddMajorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMajorBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private val TAG = "PDF_ADD_TAG"
    private lateinit var collegeArrayList: ArrayList<ModelCollege>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMajorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
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
        binding.ViewCollegeName.setOnClickListener {
            collegePickDialog()
        }
        val intent = intent
        val collegeName = intent.getStringExtra("college")
        binding.ViewCollegeName.text = collegeName

    }


    private fun validateData() {
        if (!validAll()) {
            Toast.makeText(this@AddMajorActivity, "Please All Required !!", Toast.LENGTH_SHORT).show()

        } else {
            addMajor()
            addMajorForTowRelations()
        }

    }


    private fun loadColleges() {
        collegeArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Colleges")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                collegeArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCollege::class.java)
                    collegeArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: $model")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addMajor() {
        progressDialog.setMessage("Adding Major info")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["collegeId"] = "$selectedCollegeId"
        hashMap["collegeName"] = "$selectedCollegeTitle"
        hashMap["majorName"] = "${binding.addMajorName.text.toString().trim()}"
        hashMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("Majors")
            .child(selectedCollegeTitle)
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDB: Uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this@AddMajorActivity, "Added...", Toast.LENGTH_SHORT).show()
                binding.addMajorName.text.clear()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfToStorage: Failed to upload due  to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(
                    this@AddMajorActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        val hashNoti = HashMap<String,Any>()
        hashNoti["nameCollege"] = binding.addMajorName.text.toString().trim()+" "+"has been added to the Major Go check.."
        hashNoti["id"] = "$selectedCollegeId"
        hashNoti["name"] = "${binding.addMajorName.text.toString().trim()}"
        val refNotification = FirebaseDatabase.getInstance().getReference("Notifications")
        refNotification.child("$timestamp").updateChildren(hashNoti)
    }
    private fun addMajorForTowRelations() {
      //  progressDialog.setMessage("Adding Major info")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["collegeId"] = "$selectedCollegeId"
        hashMap["collegeName"] = "$selectedCollegeTitle"
        hashMap["majorName"] = "${binding.addMajorName.text.toString().trim()}"
        hashMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("MajorsForTowRelations")
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDB: Uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this@AddMajorActivity, "Added...", Toast.LENGTH_SHORT).show()
                binding.addMajorName.text.clear()
            }
            .addOnFailureListener { e ->

                Log.d(TAG, "uploadPdfToStorage: Failed to upload due  to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(
                    this@AddMajorActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }


    private var selectedCollegeId = ""
    private var selectedCollegeTitle = ""

    private fun collegePickDialog(): Boolean {

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

                Log.d(TAG, "categoryPickDialog: Selected Category ID $selectedCollegeId")
                Log.d(TAG, "categoryPickDialog: Selected Category Title $selectedCollegeTitle")
            }
            .show()

        return true
    }

    fun validAll(): Boolean {
        return isEmty(binding.ViewCollegeName, "Required") and
                isEmty(binding.addMajorName, "Required")

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