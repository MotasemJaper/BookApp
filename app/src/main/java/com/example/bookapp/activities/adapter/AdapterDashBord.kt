package com.example.bookapp.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookapp.activities.Filters.FilterDashBord
import com.example.bookapp.activities.Model.UserDashBord
import com.example.bookapp.databinding.ListForDashbordBinding

class AdapterDashBord : RecyclerView.Adapter<AdapterDashBord.ViewHolder> , Filterable {

    private lateinit var binding: ListForDashbordBinding
    lateinit var context: Context
    private lateinit var list: ArrayList<UserDashBord>
     lateinit var filterList: ArrayList<UserDashBord>
    private var filter: FilterDashBord? = null

    constructor(context: Context, list: ArrayList<UserDashBord>) : super() {
        this.context = context
        this.list = list
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDashBord.ViewHolder {
        binding = ListForDashbordBinding.inflate(LayoutInflater.from(context),parent,false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: AdapterDashBord.ViewHolder, position: Int) {
        val model = list[position]
        holder.email.text = model.email
        holder.name.text = model.name
        holder.type.text = model.userType
        val imageUri = model.profileImage
        if (imageUri != null){
            Glide.with(context).load(imageUri).into(holder.imageProfile)
        }
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var name: TextView = binding.name
        var email: TextView = binding.email
        var imageProfile: ImageView = binding.imageProfile
//        var imageDelete: ImageView = binding.imageDelete
//        var ktt: ImageView = binding.ktt
        var type: TextView = binding.type
    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterDashBord(filterList, this)
        }
        return filter as FilterDashBord
    }
}