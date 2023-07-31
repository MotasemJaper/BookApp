package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.adapter.AdapterPdfView
import com.example.bookapp.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdf: AdapterPdfView
    private var courseIdForPdfView = ""
    private var idCourse = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfArrayList = ArrayList()
        val intent = intent
        courseIdForPdfView = intent.getStringExtra("nameCourse")!!
        idCourse = intent.getStringExtra("idCourse")!!

        Log.d("courseIdForPdfView", "onCreate: $courseIdForPdfView")
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchPdf.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterPdf.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d("s", "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        loadPdfView(courseIdForPdfView)

    }

    private fun loadPdfView(courseIdForPdfView: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelPDf = ds.getValue(ModelPdf::class.java)
                        if (modelPDf != null){
                            pdfArrayList.add(modelPDf!!)
                            adapterPdf = AdapterPdfView(this@PdfViewActivity, pdfArrayList)
                            val manger = GridLayoutManager(this@PdfViewActivity, 3)
                            binding.recyclerPdfView.adapter = adapterPdf
                            binding.recyclerPdfView.layoutManager = manger
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PdfViewActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }
}