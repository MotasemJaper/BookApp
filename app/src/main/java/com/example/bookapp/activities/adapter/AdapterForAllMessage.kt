package com.example.bookapp.activities.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.activities.DeatilActivityForAdmin
import com.example.bookapp.activities.DetailBookActivity
import com.example.bookapp.activities.Filters.FilterMessage
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.Model.Message
import com.example.bookapp.databinding.ListForAllMessageBinding
import com.github.barteksc.pdfviewer.PDFView

class AdapterForAllMessage : RecyclerView.Adapter<AdapterForAllMessage.ViewHolder> , Filterable  {

    private lateinit var binding: ListForAllMessageBinding
    lateinit var context: Context
    private lateinit var list: ArrayList<Message>
    lateinit var filterList: ArrayList<Message>
    private var filter: FilterMessage? = null

    constructor()
    constructor(
        context: Context,
        list: ArrayList<Message>
    ) : super() {
        this.context = context
        this.list = list
        this.filterList = list
    }




    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterForAllMessage.ViewHolder {
        binding = ListForAllMessageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterForAllMessage.ViewHolder, position: Int) {
        val model = list[position]
        val uri = model.url
        holder.name.text = model.name
        holder.email.text = model.email
        holder.message.text = model.message
        holder.pdfView.setOnClickListener {
            val intent = Intent(context, DeatilActivityForAdmin::class.java)
            intent.putExtra("courseId","${model.id}")
            intent.putExtra("nameCourse","${model.id}")
            Log.d("courdID", "onBindViewHolder: ${model.id}")
            context.startActivity(intent)
        }

        Log.d("asdadasdsad", "onBindViewHolder: ${model.url}")
        if (uri != null){
            MyApplication.loadPdfFromUrlSinglePage(
                model.url,
                model.name,
                holder.pdfView,
                holder.progressBar,
                null
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = binding.name
        var email: TextView = binding.email
        var pdfView: PDFView = binding.pdfView
        var message: TextView = binding.message
        var progressBar: ProgressBar = binding.progressBar
    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterMessage(filterList, this)
        }
        return filter as FilterMessage
    }
}

