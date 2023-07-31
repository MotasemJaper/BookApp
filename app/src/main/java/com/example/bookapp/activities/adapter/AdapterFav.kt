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
import com.example.bookapp.activities.Filters.FilterFav
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.ModelFav
import com.example.bookapp.databinding.ListPdfViewBinding
import com.github.barteksc.pdfviewer.PDFView

class AdapterFav : RecyclerView.Adapter<AdapterFav.ViewHolder>, Filterable {

    private lateinit var binding: ListPdfViewBinding
    var listFav: ArrayList<ModelFav>
    var filterList: ArrayList<ModelFav>
    private val context: Context
    private var filter: FilterFav? = null

    constructor(listFav: ArrayList<ModelFav>, context: Context) : super() {
        this.listFav = listFav
        this.context = context
        this.filterList = listFav
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFav.ViewHolder {
        binding = ListPdfViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterFav.ViewHolder, position: Int) {
        val model = listFav[position]
        holder.nameBook.text = model.nameBook
        holder.typeMaterial.text = model.typeMaterial
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailBookActivity::class.java)
            intent.putExtra("courseId", "${model.courseId}")
            intent.putExtra("nameCourse", "${model.nameCourse}")
            Log.d("courdID", "onBindViewHolder: ${model.courseId}")
            context.startActivity(intent)
        }
        MyApplication.loadPdfFromUrlSinglePage(
            model.url,
            model.nameBook,
            holder.pdfView,
            holder.progressBar,
            null
        )

    }

    override fun getItemCount(): Int {
        return listFav.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameBook: TextView = binding.nameBook
        var pdfView: PDFView = binding.pdfView
        var typeMaterial: TextView = binding.typeMaterial
        val progressBar = binding.progressBar
    }

    override fun getFilter(): Filter {

        if (filter == null) {
            filter = FilterFav(filterList, this)
        }
        return filter as FilterFav
    }
}