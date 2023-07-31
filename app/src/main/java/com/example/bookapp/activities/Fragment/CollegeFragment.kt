package com.example.bookapp.activities.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.bookapp.R
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.adapter.AdapterViewCollege
import com.example.bookapp.databinding.FragmentCollegeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CollegeFragment : Fragment() {

    private lateinit var binding: FragmentCollegeBinding
    private lateinit var collegeList: ArrayList<ModelCollege>
    private lateinit var adapterViewCollege: AdapterViewCollege

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCollegeBinding.inflate(layoutInflater)
        collegeList = ArrayList()
        loadCollege()

        binding.searchBook.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterViewCollege.filter.filter(s)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        return binding.root
    }

    private fun loadCollege() {
        val ref = FirebaseDatabase.getInstance().getReference("Colleges")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                collegeList.clear()
                for (ds in snapshot.children) {
                    val modelCollege = ds.getValue(ModelCollege::class.java)
                    collegeList.add(modelCollege!!)
                    adapterViewCollege = AdapterViewCollege(context!!, collegeList)
                    binding.collegeRecycler.adapter = adapterViewCollege
                    val manger = GridLayoutManager(requireContext(), 2)
                    binding.collegeRecycler.layoutManager = manger
                    adapterViewCollege.notifyDataSetChanged()


                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

}