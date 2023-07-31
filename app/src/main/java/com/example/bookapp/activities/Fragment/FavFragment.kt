package com.example.bookapp.activities.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.adapter.AdapterPdfView
import com.example.bookapp.databinding.FragmentCollegeBinding
import com.example.bookapp.databinding.FragmentFavBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavFragment : Fragment() {

    private lateinit var binding: FragmentFavBinding
    private lateinit var favList: ArrayList<ModelPdf>
    private lateinit var adapterFav: AdapterPdfView
    private lateinit var mAuth: FirebaseAuth

    var nameCourse: String = ""
    var idCourse: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()
        favList = ArrayList()
        loadNameFav()

        binding.searchCourse.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterFav.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        return binding.root
    }


    private fun loadAllFav(idCourse: String) {

        val ref = FirebaseDatabase.getInstance().getReference("PdfForTowRelation")
            .orderByChild("id").equalTo(idCourse).limitToLast(10)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        Log.d("nameBook", "onDataChange: ${model!!.nameBook}")
                        Log.d("nameBook", "onDataChange: ${model!!.id}")
                        favList.add(model!!)

                        adapterFav = AdapterPdfView(requireContext(), favList)
                        val manger = GridLayoutManager(requireContext(), 3)
                        binding.recyclerFav.adapter = adapterFav
                        binding.recyclerFav.layoutManager = manger

                    }

                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
    }

    private fun loadNameFav() {
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid!!)
            .child("Favorites")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    idCourse = ds.child("idCourse").value.toString()
                    nameCourse = ds.child("nameCourse").value.toString()
                    Log.d("idCourse", "onDataChange: $idCourse")
                    loadAllFav(idCourse)
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

}