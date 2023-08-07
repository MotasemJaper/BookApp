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
import com.example.bookapp.activities.EditCollegeActivity
import com.example.bookapp.activities.EditMajorActivity
import com.example.bookapp.activities.Filters.FilterMajor
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.databinding.ItemForMajorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterViewMajors : RecyclerView.Adapter<AdapterViewMajors.ViewHolder> , Filterable {


    private lateinit var binding: ItemForMajorBinding
    private val context: Context
    public var modelMajor: ArrayList<ModelMajor>
    private var filterList: ArrayList<ModelMajor>
    private  var  filter : FilterMajor?= null

    private lateinit var mAuth: FirebaseAuth

    constructor(context: Context, modelMajor: ArrayList<ModelMajor>) {
        this.context = context
        this.modelMajor = modelMajor
        this.filterList = modelMajor
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewMajors.ViewHolder {
        binding = ItemForMajorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterViewMajors.ViewHolder, position: Int) {
        val college = modelMajor[position]
        val id = college.id
        val nameCollege = college.majorName
        val uid = college.uid
        val timestamp = college.timestamp
        Log.d("iasdasdasdasdd", "onBindViewHolder: $nameCollege")

        holder.nameCollege.text = nameCollege
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CoursesActivity::class.java)
            intent.putExtra("majorName",nameCollege)
            context.startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null){
            val ref  = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.child("userType").value.toString()
                    if (user.equals("user")){
                        holder.menuMajors.visibility = View.INVISIBLE
                    }

                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
        }else{
            Toast.makeText(context, "you must login !!", Toast.LENGTH_SHORT).show()
        }


        holder.menuMajors.setOnClickListener {
            moreOptionDialog(college,holder)
        }
    }

    private fun moreOptionDialog(model: ModelMajor, holder: AdapterViewMajors.ViewHolder) {

        val collegeName = model.collegeName
        val id = model.id
        val option = arrayOf("Edit","Delete")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(option){ dialog, position->

                if(position == 0 ){
                    val intent = Intent(context, EditMajorActivity::class.java)
                    intent.putExtra("collegeId",collegeName)
                    intent.putExtra("id",id)
                    context.startActivity(intent)

                }else if (position == 1){

                    deleteMajors(collegeName,model.id)

                }
            }
            .show()
    }

    private fun deleteMajors(majorName: String, id: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("please wait !!")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val ref = FirebaseDatabase.getInstance().getReference("Majors").child(majorName).child(id)
        ref.removeValue().addOnCompleteListener {task->
            if (task.isSuccessful){
                progressDialog.dismiss()
                Toast.makeText(context, "deleted!!", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener { e ->

                progressDialog.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()

            }
    }



    override fun getItemCount(): Int {
        return modelMajor.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCollege: ImageView = binding.imageMajors
        var nameCollege: TextView = binding.nameMajors
        var menuMajors: ImageView = binding.menuMajors

    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterMajor(filterList, this)
        }
        return filter as FilterMajor
    }
}