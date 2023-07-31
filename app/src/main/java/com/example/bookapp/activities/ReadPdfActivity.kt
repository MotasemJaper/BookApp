package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.Constants
import com.example.bookapp.databinding.ActivityReadPdfBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ReadPdfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadPdfBinding


    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    var idCourse = ""
    var courseName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReadPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idCourse = intent.getStringExtra("idCourse")!!
        courseName = intent.getStringExtra("courseName")!!
        loadBookDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get pdf URL from db")

        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
        ref.child(courseName).child(idCourse)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: $pdfUrl")
                    loadBookFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get pdf firebase storage using URl")

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->

                Log.d(TAG, "loadBookFromUrl: pdf got from URl")

                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange { page, pageCount ->

                        val currentPage = page + 1
                        binding.toolbarSubtitleTv.text = "$currentPage/$pageCount"
                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
                    }
                    .onError { t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "loadBookFromUrl: ${t.message}")
                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->

                Log.d(TAG, "loadBookFromUrl: Failed to get pdf due to ${e.message}")
                binding.progressBar.visibility = View.GONE
            }
    }
}