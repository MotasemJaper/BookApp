package com.example.bookapp.activities.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
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
import com.bumptech.glide.Glide
import com.example.bookapp.activities.EditCollegeActivity
import com.example.bookapp.activities.Filters.FilterCollege
import com.example.bookapp.activities.Filters.MyApplication
import com.example.bookapp.activities.MajorsActivity
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.databinding.ItemForCollegeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterViewCollege : RecyclerView.Adapter<AdapterViewCollege.ViewHolder>, Filterable {

    private lateinit var binding: ItemForCollegeBinding
    private val context: Context
    public var modelCollege: ArrayList<ModelCollege>
    private var filterList: ArrayList<ModelCollege>
    private  var  filter : FilterCollege?= null

    private lateinit var mAuth: FirebaseAuth

    constructor(context: Context, modelCollege: ArrayList<ModelCollege>) {
        this.context = context
        this.modelCollege = modelCollege
        this.filterList = modelCollege
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewCollege.ViewHolder {
        binding = ItemForCollegeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterViewCollege.ViewHolder, position: Int) {
        val college = modelCollege[position]
        val id = college.id
        val nameCollege = college.college
        val uid = college.uid
        val timestamp = college.timestamp
        val imageUri = college.imageUri

        holder.nameCollege.text = nameCollege
        if (imageUri != null){
            Glide.with(context).load(imageUri).into(holder.imageCollege)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context,MajorsActivity::class.java)
            intent.putExtra("college",nameCollege)
            Log.d("id", "onBindViewHolder: $id")
            context.startActivity(intent)
        }

        mAuth =FirebaseAuth.getInstance()
        val ref  = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid!!)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.child("userType").value.toString()
                if (user.equals("user")){
                    holder.menuCollege.visibility = View.INVISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })

        holder.menuCollege.setOnClickListener {
            moreOptionDialog(college,holder)
        }


    }

    private fun moreOptionDialog(model: ModelCollege, holder: ViewHolder) {

        val bookId = model.id
        val option = arrayOf("Edit","Delete")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(option){ dialog, position->

                if(position == 0 ){
                    val intent = Intent(context, EditCollegeActivity::class.java)
                    intent.putExtra("collegeId",bookId)
                    context.startActivity(intent)

                }else if (position == 1){

                    deleteCollege(model.id)

                }
            }
            .show()
    }

    private fun deleteCollege(id: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("please wait !!")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val ref = FirebaseDatabase.getInstance().getReference("Colleges").child(id)
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
        return modelCollege.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageCollege: ImageView = binding.imageCollege
        var nameCollege: TextView = binding.nameCollege
        var menuCollege: ImageView = binding.menuCollege
    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterCollege(filterList, this)
        }
        return filter as FilterCollege
    }

}