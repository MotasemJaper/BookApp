package com.example.bookapp.activities.adapter

import android.app.ProgressDialog
import android.content.Context
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
import com.example.bookapp.activities.Filters.FilterRequest
import com.example.bookapp.activities.Model.UserDashBord
import com.example.bookapp.databinding.ListForRequestBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterViewRequest : RecyclerView.Adapter<AdapterViewRequest.ViewHolder>, Filterable {

    private lateinit var binding: ListForRequestBinding
    lateinit var context: Context
    private lateinit var list: ArrayList<UserDashBord>
    lateinit var filterList: ArrayList<UserDashBord>
    private var filter: FilterRequest? = null
    var idReq = ""


    constructor(context: Context, list: ArrayList<UserDashBord>) : super() {
        this.context = context
        this.list = list
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterViewRequest.ViewHolder {
        binding = ListForRequestBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterViewRequest.ViewHolder, position: Int) {
        val model = list[position]
        idReq = model.id
        holder.email.text = model.email
        holder.name.text = model.name
        holder.unId.text = model.unId
        holder.gpa.text = model.gpa
        val imageUri = model.profileImage
        if (imageUri != null) {
            Glide.with(context).load(imageUri).into(holder.imageProfile)
        }

        holder.imageAccept.setOnClickListener {
            accapetRequest(model.Uid)
        }

        holder.imageDelete.setOnClickListener {
            deleteRequest(model.id)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = binding.name
        var email: TextView = binding.email
        var unId: TextView = binding.Id
        var gpa: TextView = binding.gpa
        var imageProfile: ImageView = binding.imageProfile
        var imageDelete: ImageView = binding.imageDelete
        var imageAccept: ImageView = binding.accept
    }

    private fun deleteRequest(id: String) {
        val process = ProgressDialog(context)
        process.setMessage("Please Wait !!")
        process.setCancelable(false)
        process.show()
        val ref = FirebaseDatabase.getInstance().getReference("RequestPermission").child(id)
        ref.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                process.dismiss()
                Toast.makeText(context, "Requests Deleted!!", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener { e ->
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                process.dismiss()
            }
    }

    private fun accapetRequest(id: String) {
        val process = ProgressDialog(context)
        process.setMessage("Please Wait !!")
        process.setCancelable(false)
        process.show()
        val hasMap = HashMap<String, Any?>()
        hasMap["userType"] = "admin"
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(id)
        ref.updateChildren(hasMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reff = FirebaseDatabase.getInstance().getReference("RequestPermission").child(idReq)
                reff.removeValue()
                process.dismiss()
                Toast.makeText(context, "Done !!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
                process.dismiss()
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }


    }

    override fun getFilter(): Filter {

        if (filter == null) {
            filter = FilterRequest(filterList, this)
        }
        return filter as FilterRequest
    }
}