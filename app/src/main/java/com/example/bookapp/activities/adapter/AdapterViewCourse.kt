package com.example.bookapp.activities.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookapp.activities.CoursesActivity
import com.example.bookapp.activities.EditCourseActivity
import com.example.bookapp.activities.EditMajorActivity
import com.example.bookapp.activities.Filters.FilterCourse
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.activities.PdfViewActivity
import com.example.bookapp.databinding.ItemForCourseBinding
import com.google.firebase.database.FirebaseDatabase

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

        holder.menuCourse.setOnClickListener {
            moreOptionDialog(course,holder)
        }



    }

    private fun moreOptionDialog(model: ModelCourse, holder: AdapterViewCourse.ViewHolder) {

        val majorName = model.majorName
        val courseName = model.courseName
        val id = model.id
        val level = model.level
        val option = arrayOf("Edit","Delete")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(option){ dialog, position->

                if(position == 0 ){
                    val intent = Intent(context, EditCourseActivity::class.java)
                    intent.putExtra("id",id)
                    intent.putExtra("level",level)
                    intent.putExtra("majorName",majorName)
                    context.startActivity(intent)

                }else if (position == 1){

                    deleteMajors(courseName,model.id)

                }
            }
            .show()
    }

    private fun deleteMajors(courseName: String, id: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("please wait !!")
        progressDialog.setCancelable(false)
        progressDialog.show()
        Log.d("courseName", "deleteMajors: $courseName")
        val ref = FirebaseDatabase.getInstance().getReference("Courses").child(courseName)
        ref.removeValue().addOnCompleteListener {task->
            if (task.isSuccessful){
                progressDialog.dismiss()
                val reff = FirebaseDatabase.getInstance().getReference("CoursesForRelations").child(courseName)
                reff.removeValue()
                Toast.makeText(context, "deleted!!", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    override fun getItemCount(): Int {
        return modelCourse.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCourse: ImageView = binding.imageCourse
        var nameCourse: TextView = binding.nameCourse
        var menuCourse: ImageView = binding.menuCourse
    }

    override fun getFilter(): Filter {

        if (filter == null){
           filter = FilterCourse(filterList,this)
        }
        return filter as FilterCourse
    }
}