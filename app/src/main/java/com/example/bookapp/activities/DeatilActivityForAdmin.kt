package com.example.bookapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.Constants
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.databinding.ActivityDeatilForAdminBinding
import com.example.bookapp.databinding.ActivityDetailBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class DeatilActivityForAdmin : AppCompatActivity() {


    private lateinit var binding: ActivityDeatilForAdminBinding
    private var courseIdForPdfView = ""
    private var idCourse = ""
    private var url = ""

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var isInMyFavorite = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeatilForAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            checkIsFavorite()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        val intent = intent
        courseIdForPdfView = intent.getStringExtra("nameCourse")!!
        Log.d("courseIdForPdfView", "onCreate: $courseIdForPdfView")
        idCourse = intent.getStringExtra("courseId")!!
        Log.d("idCourse", "onCreate: $idCourse")
        MyApplication.incrementBookViewCount(courseIdForPdfView, idCourse)


        loadPdfView(courseIdForPdfView, idCourse)

        binding.downloadBookBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadBook()
            } else {
                requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this@DeatilActivityForAdmin, ReadPdfActivity::class.java)
            intent.putExtra("idCourse", idCourse)
            intent.putExtra("courseName", courseIdForPdfView)
            startActivity(intent)
        }

        binding.imageFav.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(this, "You're not logged in ", Toast.LENGTH_SHORT).show()
            } else {
                if (isInMyFavorite) {
                    removeFromFavorite()
                } else {
                    addToFavorite()
                }
            }
        }
    }

    private fun addToFavorite() {

        val timestamp = System.currentTimeMillis()

        val hasMap = HashMap<String, Any>()
        hasMap["idCourse"] = idCourse
        hasMap["nameCourse"] = courseIdForPdfView
        hasMap["timestamp"] = "$timestamp"

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(idCourse)
            .setValue(hasMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Added To Favorite", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this, " Failed to add to fav ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun removeFromFavorite() {

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(idCourse)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Remove to favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    " Failed to remove from fav due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun checkIsFavorite() {
        Log.d("asdasdasdasdasd", "checkIsFavorite: Checking if book is in av or not $idCourse")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
            .child(firebaseAuth.uid!!)
            .child("Favorites").child(idCourse)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.child(idCourse).exists()
                    if (isInMyFavorite) {
                        binding.imageFav.setImageResource(R.drawable.favoriteunselect)

                    } else {
                        binding.imageFav.setImageResource(R.drawable.favoriteselect)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun saveToDownloadFolder(bytes: ByteArray?) {
        val nameWithExtention = "${System.currentTimeMillis()}.pdf"

        try {
            val downloadsFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdirs()
            val filePath = downloadsFolder.path + "/" + nameWithExtention
            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()
            Toast.makeText(this, " saved to Downloads Folder", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
            incrementDownloadCount()
        } catch (e: Exception) {
            progressDialog.dismiss()
            Toast.makeText(this, "  Failed to save due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementDownloadCount() {

        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
            .child(courseIdForPdfView)
            .child(idCourse)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var downloadCount = "${snapshot.child("downloadsCount").value}"

                    if (downloadCount == "" || downloadCount == "null") {
                        downloadCount = "0"
                    }

                    val newDownLoadCount = downloadCount.toLong() + 1

                    val hasMap: HashMap<String, Any> = HashMap()
                    hasMap["downloadsCount"] = newDownLoadCount

                    val dbRef = FirebaseDatabase.getInstance().getReference("Pdf")
                    dbRef.child(courseIdForPdfView).child(idCourse)
                        .updateChildren(hasMap)
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener { e ->
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun downloadBook() {
        progressDialog.setMessage("DownLoading Book")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->

                saveToDownloadFolder(bytes)
            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    " Failed to download book due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadPdfView(courseIdForPdfView: String, courseId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
            .child(courseId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    val description = "${snapshot.child("description").value}"
                    val nameBook = "${snapshot.child("nameBook").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val nameCourse = "${snapshot.child("nameCourse").value}"
                    val nameMajor = "${snapshot.child("nameMajor").value}"
                    val typeMaterial = "${snapshot.child("typeMaterial").value}"
                    url = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    MyApplication.loadPdfFromUrlSinglePage(
                        "$url",
                        "$nameBook",
                        binding.pdfView,
                        binding.progressBar,
                        null
                    )
                    MyApplication.loadPdfSize("$url", "$nameCourse", binding.numberOfSize)

                    binding.nameBook.text = nameBook
                    binding.courseName.text = nameCourse
                    binding.numberOfViews.text = viewsCount
                    binding.numberOfDownloads.text = downloadsCount
                    binding.desBook.text = description
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DeatilActivityForAdmin, "${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            if (isGranted) {

                downloadBook()
            } else {

                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
