package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.ModelCollege
import com.example.bookapp.activities.adapter.AdapterViewCollege

class FilterCollege : Filter {

    private var filterList: ArrayList<ModelCollege>
    private var adapterCategory: AdapterViewCollege

    constructor(filterList: ArrayList<ModelCollege>, adapterCategory: AdapterViewCollege) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<ModelCollege> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].college.uppercase().contains(constraint)) {
                    filterModel.add(filterList[i])
                }
            }
            results.count = filterModel.size
            results.values = filterModel
        } else {
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterCategory.modelCollege = results.values as ArrayList<ModelCollege>
        adapterCategory.notifyDataSetChanged()
    }

}