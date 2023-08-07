package com.example.bookapp.activities.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.activities.DetailBookActivity
import com.example.bookapp.activities.Filters.FilterPdf
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.PdfViewActivity
import com.example.bookapp.databinding.ListPdfViewBinding
import com.github.barteksc.pdfviewer.PDFView

class AdapterPdfView : RecyclerView.Adapter<AdapterPdfView.ViewHolder>, Filterable {

    private lateinit var binding: ListPdfViewBinding
    private val context: Context
    var modelPdfList: ArrayList<ModelPdf>
    private var filterList: ArrayList<ModelPdf>
    private  var  filter : FilterPdf?= null


    constructor(context: Context, modelPdf: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.modelPdfList = modelPdf
        this.filterList = modelPdf
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPdfView.ViewHolder {
        binding = ListPdfViewBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterPdfView.ViewHolder, position: Int) {

        val modelPdf = modelPdfList[position]
        val uri = modelPdf.url
        holder.nameBook.text = modelPdf.nameCourse
        holder.typeMaterial.text = modelPdf.typeMaterial
        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailBookActivity::class.java)
            intent.putExtra("courseId","${modelPdf.id}")
            intent.putExtra("nameCourse","${modelPdf.nameCourse}")
            Log.d("courdID", "onBindViewHolder: ${modelPdf.id}")
            context.startActivity(intent)
        }


        Log.d("url", "onBindViewHolder: ${modelPdf.url}")
        if (uri != null){
            MyApplication.loadPdfFromUrlSinglePage(
                modelPdf.url,
                modelPdf.nameBook,
                holder.pdfView,
                holder.progressBar,
                null
            )
        }


    }

    override fun getItemCount(): Int {
        return modelPdfList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameBook: TextView = binding.nameBook
        var pdfView: PDFView = binding.pdfView
        var typeMaterial: TextView = binding.typeMaterial
        val progressBar = binding.progressBar
    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterPdf(filterList,this)
        }
        return filter as FilterPdf
    }
}