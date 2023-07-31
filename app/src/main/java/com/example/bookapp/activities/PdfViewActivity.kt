package com.example.bookapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.Model.ModelPdfViewForBook
import com.example.bookapp.activities.Model.ModleSlidesPdfView
import com.example.bookapp.activities.adapter.AdapterPdfView
import com.example.bookapp.activities.adapter.AdapterPdfViewForBook
import com.example.bookapp.activities.adapter.AdapterSlidesPdfView
import com.example.bookapp.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewBinding
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var listBook: ArrayList<ModelPdfViewForBook>
    private lateinit var listSlides: ArrayList<ModleSlidesPdfView>
    private lateinit var adapterPdf: AdapterPdfView
    private lateinit var adapterPdfViewForBook: AdapterPdfViewForBook
    private lateinit var adapterSlidesPdfView: AdapterSlidesPdfView
    private var courseIdForPdfView = ""
    private var idCourse = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfArrayList = ArrayList()
        listBook = ArrayList()
        listSlides = ArrayList()
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

        loadBookView(courseIdForPdfView)
        loadSlidesPdfView(courseIdForPdfView)
        loadExamPdfView(courseIdForPdfView)

    }

    private fun loadBookView(courseIdForPdfView: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
            .orderByChild("typeMaterial").equalTo("Book")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listBook.clear()
                    for (ds in snapshot.children) {
                        val modelPDf = ds.getValue(ModelPdfViewForBook::class.java)
                        Log.d("typeMaterial", "onDataChange: ${modelPDf!!.typeMaterial}")
                        Log.d("typeMaterial", "onDataChange: ${modelPDf!!.id}")
                        if (modelPDf != null) {
                            listBook.add(modelPDf!!)
                            adapterPdfViewForBook = AdapterPdfViewForBook(this@PdfViewActivity, listBook)
                            val manger = LinearLayoutManager(
                                this@PdfViewActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            binding.recyclerBookView.adapter = adapterPdfViewForBook
                            binding.recyclerBookView.layoutManager = manger
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PdfViewActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }

    private fun loadSlidesPdfView(courseIdForPdfView: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
            .orderByChild("typeMaterial").equalTo("Slides/Summery")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelPDf = ds.getValue(ModleSlidesPdfView::class.java)
                        if (modelPDf != null) {
                            listSlides.add(modelPDf!!)
                            adapterSlidesPdfView = AdapterSlidesPdfView(this@PdfViewActivity, listSlides)
                            val manger = GridLayoutManager(this@PdfViewActivity, 3)
                            binding.recyclerPdfView.adapter = adapterSlidesPdfView
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

    private fun loadExamPdfView(courseIdForPdfView: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Pdf")
            .child(courseIdForPdfView)
            .orderByChild("typeMaterial").equalTo("Exams")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val modelPDf = ds.getValue(ModelPdf::class.java)
                        if (modelPDf != null) {
                            pdfArrayList.add(modelPDf!!)
                            adapterPdf = AdapterPdfView(this@PdfViewActivity, pdfArrayList)
                            val manger = GridLayoutManager(this@PdfViewActivity, 3)
                            binding.recyclerExamView.adapter = adapterPdf
                            binding.recyclerExamView.layoutManager = manger
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