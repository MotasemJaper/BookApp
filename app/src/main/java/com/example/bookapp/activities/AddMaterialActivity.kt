package com.example.bookapp.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bookapp.activities.Model.ModelBook
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.databinding.ActivityAddMaterialBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMaterialBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var courseArrayList: ArrayList<ModelCourse>
    private lateinit var bookArrayList: ArrayList<ModelBook>
    private lateinit var majorArrayList: ArrayList<ModelMajor>
    private lateinit var collegeArrayList: ArrayList<ModelCollege>
    private var collegeID: String = ""
    private var majorId: String = ""
    private var courseId: String = ""
    private var bookId: String = ""
    private var nameCollege: String = ""
    private var nameMajor: String = ""
    private var nameCourse: String = ""
    private var nameBook: String = ""
    private var bookDescription: String = ""
    private var selectedBookId = ""
    private var selectedBookTitle = ""
    private var pdfUri: Uri? = null
    private var typeMaterial: String = ""

    private var selectedMajorId = ""
    private var selectedMajorTitle = ""
    private var selectedCollegeTitle = ""
    private var selectedCollegeId = ""
    private var selectedCourseId = ""
    private var selectedCourseTitle = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        collegeArrayList = ArrayList()
        majorArrayList = ArrayList()
        courseArrayList = ArrayList()
        bookArrayList = ArrayList()


        loadColleges()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnUploadPdf.setOnClickListener {
            validateData()
        }
//        binding.ViewBookName.setOnClickListener {
//
//        }
        binding.getPdf.setOnClickListener {
            pdfPickIntent()
        }
        binding.selectType.setOnClickListener {
            selectTypeMaterial()
        }

        binding.ViewCollegeName.setOnClickListener {
            collegePickDialog()
        }
        binding.ViewMajorName.setOnClickListener {
            majorPickDialog()
        }
        binding.ViewCourseName.setOnClickListener {
            coursePickDialog()
        }
    }

    private fun validateData() {

        if (!validAll()) {
            Toast.makeText(this@AddMaterialActivity, "Please All Required !!", Toast.LENGTH_SHORT)
                .show()
        } else {
            uploadPdfToStorage()
        }
    }

    private fun loadColleges() {
        val ref = FirebaseDatabase.getInstance().getReference("Colleges")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                collegeArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCollege::class.java)
                    Log.d("id", "onDataChange: ${model!!.id}")
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
                    Log.d("majorName", "onDataChange: ${model!!.majorName}")
                    majorArrayList.add(model!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadCourses(selectedMajorTitle: String) {
        val ref = FirebaseDatabase.getInstance().getReference("CoursesForRelations")
            .child(selectedMajorTitle)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                courseArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCourse::class.java)
                    nameCourse = model!!.courseName
                    majorId = model!!.majorId
                    Log.d("majorId", "onDataChangeMajorID: ${model!!.majorId}")
                    courseArrayList.add(model!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadBook(selectedCourseTitle: String) {

        val ref = FirebaseDatabase.getInstance().getReference("Books").child(selectedCourseTitle)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookArrayList.clear()
                for (ds in snapshot.children) {
                    val idBook = ds.getValue(ModelBook::class.java)
                    bookId = idBook!!.id
                    bookDescription = idBook.bookDescription
                    nameBook = idBook.bookName
                    bookArrayList.add(idBook!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun uploadPdfToStorage() {
        if (pdfUri == null) {
            Toast.makeText(
                this@AddMaterialActivity,
                "Please Select PDF File !!",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            progressDialog.setMessage("Uploading PDF...")
            progressDialog.show()
            val timestamp = System.currentTimeMillis()
            val filePathAndName = "Pdf/$timestamp"
            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(pdfUri!!)
                .addOnSuccessListener { taskSnapshot ->

                    val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val uploadedPdfUri = "${uriTask.result}"
                    uploadPdfInfoToDB(uploadedPdfUri, timestamp)
                    uploadPdfInfoToDBForTowRelations(uploadedPdfUri, timestamp)
                }
                .addOnFailureListener { e ->

                    progressDialog.dismiss()
                    Toast.makeText(
                        this@AddMaterialActivity,
                        "Failed to upload due  to ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun uploadPdfInfoToDB(uploadedPdfUri: String, timestamp: Long) {
        progressDialog.setMessage("Uploading PDF info")
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["description"] = "$bookDescription"
        hashMap["majorId"] = "$selectedMajorId"
        hashMap["nameMajor"] = "$selectedMajorTitle"
        hashMap["courseId"] = "$selectedCourseId"
        hashMap["nameCourse"] = "$selectedCourseTitle"
        hashMap["bookId"] = "$bookId"
        hashMap["nameBook"] = "$selectedBookTitle"
        hashMap["url"] = "$uploadedPdfUri"
        hashMap["typeMaterial"] = "$typeMaterial"
        hashMap["timestamp"] = "$timestamp"
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
        ref.child(selectedCourseTitle).child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@AddMaterialActivity, "Uploaded...", Toast.LENGTH_SHORT).show()
                pdfUri = null

            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(
                    this@AddMaterialActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadPdfInfoToDBForTowRelations(uploadedPdfUri: String, timestamp: Long) {
        progressDialog.setMessage("Uploading PDF info")
        val uid = firebaseAuth.uid
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["description"] = "$bookDescription"
        hashMap["majorId"] = "$selectedMajorId"
        hashMap["nameMajor"] = "$selectedMajorTitle"
        hashMap["courseId"] = "$selectedCourseId"
        hashMap["nameCourse"] = "$selectedCourseTitle"
        hashMap["bookId"] = "$bookId"
        hashMap["nameBook"] = "$selectedBookTitle"
        hashMap["url"] = "$uploadedPdfUri"
        hashMap["typeMaterial"] = "$typeMaterial"
        hashMap["timestamp"] = "$timestamp"
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
        ref.child("$timestamp").setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@AddMaterialActivity, "Uploaded...", Toast.LENGTH_SHORT).show()
                pdfUri = null

            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(
                    this@AddMaterialActivity,
                    "Failed to upload due  to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun selectTypeMaterial() {
        val view: View = LayoutInflater.from(this)
            .inflate(com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)

        alertDialogBuilder.setTitle("Select Material Type..")
        alertDialogBuilder.setCancelable(true)
        val type = arrayOf("Slides/Summery", "Book", "Exams")
        alertDialogBuilder.setItems(type) { dialog, which ->
            typeMaterial = type[which].toString()
            binding.selectType.text = typeMaterial
            Log.d("slides", "selectTypeMaterial: $typeMaterial")
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun bookPickDialog() {
        val bookArray = arrayOfNulls<String>(bookArrayList.size)
        for (i in bookArrayList.indices) {
            bookArray[i] = bookArrayList[i].bookName
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick a Books")
            .setItems(bookArray) { dialog, which ->

                if (bookArrayList[which] != null){

                    selectedBookTitle = bookArrayList[which].bookName
                    selectedBookId = bookArrayList[which].courseId
                    bookId = bookArrayList[which].id
                    nameBook = bookArrayList[which].bookName
                    courseId = bookArrayList[which].courseId
                    bookDescription = bookArrayList[which].bookDescription
                    Log.d("selectedBookId", "coursePickDialog: $selectedBookId")
                   // binding.ViewBookName.text = selectedBookTitle
                }


            }

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
                loadBook(selectedCourseTitle)

            }
            .show()
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
                loadCourses(selectedMajorTitle)


            }
            .show()
    }

    private fun pdfPickIntent() {

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {

                pdfUri = result.data!!.data
            } else {


                Toast.makeText(this@AddMaterialActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun validAll(): Boolean {
        return isEmty(binding.ViewCollegeName, "Required") and
                isEmty(binding.ViewMajorName, "Required") and
                isEmty(binding.ViewCourseName, "Required") and
                isEmty(binding.selectType, "Required")
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