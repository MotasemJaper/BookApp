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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.Constants
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.adapter.AdapterPdfView
import com.example.bookapp.databinding.ActivityDetailBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class DetailBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBookBinding
    private var courseIdForPdfView = ""
    private var idCourse = ""
    private var url = ""

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var isInMyFavorite = false

    private companion object {
        const val TAG = "BOOK_DETAILS_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBookBinding.inflate(layoutInflater)
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
                Log.d(TAG, "onCreate: Storage permission is already granted")
                downloadBook()
            } else {
                Log.d(TAG, "onCreate: Storage permission was not granted, LETS Request it")
                requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this@DetailBookActivity, ReadPdfActivity::class.java)
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
        Log.d(TAG, "addToFavorite: Adding to fav")

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
                Log.d(TAG, "addToFavorite: Added to fav ")
            }
            .addOnFailureListener { e ->

                Log.d(TAG, "addToFavorite: Failed to add to fav ${e.message}")
                Toast.makeText(this, " Failed to add to fav ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun removeFromFavorite() {
        Log.d(TAG, "removeFromFavorite: Removing from fav")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(idCourse)
            .removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "removeFromFavorite: removed from fav")
                Toast.makeText(this, "Remove to favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Log.d(TAG, "removeFromFavorite: Failed to remove from fav due to ${e.message}")
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
                        Log.d(TAG, "onDataChange: available in favorite")
                        binding.imageFav.setImageResource(R.drawable.favoriteunselect)

                    } else {
                        Log.d(TAG, "onDataChange: not available in favorite")
                        binding.imageFav.setImageResource(R.drawable.favoriteselect)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun saveToDownloadFolder(bytes: ByteArray?) {
        Log.d(TAG, "saveToDownloadFolder: saving download book")
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
            Log.d(TAG, "saveToDownloadFolder: saved to Downloads Folder")
            progressDialog.dismiss()
            incrementDownloadCount()
        } catch (e: Exception) {
            progressDialog.dismiss()
            Log.d(TAG, "saveToDownloadFolder: Failed to save due to ${e.message}")
            Toast.makeText(this, "  Failed to save due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun incrementDownloadCount() {
        Log.d(TAG, "incrementDownloadCount: ")

        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
            .child(idCourse)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var downloadCount = "${snapshot.child("downloadsCount").value}"
                    Log.d(TAG, "onDataChange: Current Downloads Count $downloadCount")

                    if (downloadCount == "" || downloadCount == "null") {
                        downloadCount = "0"
                    }

                    val newDownLoadCount = downloadCount.toLong() + 1
                    Log.d(TAG, "onDataChange: New Downloads Count $downloadCount")

                    val hasMap: HashMap<String, Any> = HashMap()
                    hasMap["downloadsCount"] = newDownLoadCount

                    val dbRef = FirebaseDatabase.getInstance().getReference("Pdf")
                    dbRef.child(courseIdForPdfView).child(idCourse)
                        .updateChildren(hasMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "onDataChange: Downloads count incremented")
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "onDataChange: Failed to increment due to ${e.message}")
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun downloadBook() {
        Log.d(TAG, "downloadBook: DownLoading Book")
        progressDialog.setMessage("DownLoading Book")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->

                Log.d(TAG, "downloadBook: Book downloaded...")
                saveToDownloadFolder(bytes)
            }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Log.d(TAG, "downloadBook: Failed to download book due to ${e.message}")
                Toast.makeText(
                    this,
                    " Failed to download book due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun loadPdfView(courseIdForPdfView: String, courseId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
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
                    Toast.makeText(this@DetailBookActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

            if (isGranted) {

                Log.d(TAG, "onCreate: Storage permission is  granted")
                downloadBook()
            } else {

                Log.d(TAG, "onCreate: Storage permission  is denied")
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

}