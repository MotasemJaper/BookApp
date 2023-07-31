package com.example.bookapp.activities.Fragment

import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.adapter.AdapterPdfView
import com.example.bookapp.databinding.FragmentHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeAdminFragment : Fragment() {

    private lateinit var binding: FragmentHomeAdminBinding
    private lateinit var lis: ArrayList<ModelPdf>
    private lateinit var adapter: AdapterPdfView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        lis = ArrayList()

        loadMostViewDownloadedBooks("downloadsCount")
        loadMostViewedBooks("viewsCount")
        binding.searchBook.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        return binding.root
    }

    private fun loadMostViewDownloadedBooks(orderBy: String) {
        lis = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
        ref.orderByChild(orderBy).limitToFirst(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    lis.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelPdf::class.java)
                        lis.add(model!!)
                    }

                    adapter = AdapterPdfView(context!!,lis)
                    binding.recMostDownloadsAdmin.adapter = adapter
                    val manger = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.recMostDownloadsAdmin.layoutManager = manger
                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {



                }
            })
    }

    private fun loadMostViewedBooks(orderBy: String) {

        lis = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
        ref.orderByChild(orderBy).limitToFirst(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    lis.clear()
                    for (ds in snapshot.children){
                        val model = ds.getValue(ModelPdf::class.java)
                        lis.add(model!!)
                    }

                    adapter = AdapterPdfView(context!!,lis)
                    binding.mostViewsAdmin.adapter = adapter
                    val manger = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.mostViewsAdmin.layoutManager = manger
                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {



                }
            })
    }




}