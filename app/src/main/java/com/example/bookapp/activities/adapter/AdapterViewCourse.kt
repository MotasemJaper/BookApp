package com.example.bookapp.activities.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.activities.CoursesActivity
import com.example.bookapp.activities.Filters.FilterCourse
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.PdfViewActivity
import com.example.bookapp.databinding.ItemForCourseBinding

class AdapterViewCourse: RecyclerView.Adapter<AdapterViewCourse.ViewHolder> , Filterable {
    private lateinit var binding: ItemForCourseBinding
    private val context: Context
    public var modelCourse: ArrayList<ModelCourse>
    private var filterList: ArrayList<ModelCourse>
    private  var  filter : FilterCourse?= null

    constructor(context: Context, modelCourse: ArrayList<ModelCourse>){
        this.context = context
        this.modelCourse = modelCourse
        this.filterList = modelCourse
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewCourse.ViewHolder {
        binding = ItemForCourseBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterViewCourse.ViewHolder, position: Int) {
        val course = modelCourse[position]
        val id = course.id
        val nameCourse = course.courseName
        val uid = course.uid
        val majorId = course.majorId
        val timestamp = course.timestamp

        holder.nameCourse.text = nameCourse
        holder.itemView.setOnClickListener {
            val intent = Intent(context,PdfViewActivity::class.java)
            intent.putExtra("nameCourse",nameCourse)
            intent.putExtra("idCourse",id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return modelCourse.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCourse: ImageView = binding.imageCourse
        var nameCourse: TextView = binding.nameCourse
    }

    override fun getFilter(): Filter {

        if (filter == null){
           filter = FilterCourse(filterList,this)
        }
        return filter as FilterCourse
    }
}