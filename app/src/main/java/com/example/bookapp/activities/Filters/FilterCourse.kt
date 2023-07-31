package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.ModelCourse
import com.example.bookapp.activities.adapter.AdapterViewCourse

class FilterCourse: Filter {
    private var filterList: ArrayList<ModelCourse>
    private var adapterCourse: AdapterViewCourse

    constructor(filterList: ArrayList<ModelCourse>, adapterCourse: AdapterViewCourse) : super() {
        this.filterList = filterList
        this.adapterCourse = adapterCourse
    }

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint = constraint
        val results = Filter.FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<ModelCourse> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].courseName.uppercase().contains(constraint)) {
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

    override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
        adapterCourse.modelCourse = results.values as ArrayList<ModelCourse>
        adapterCourse.notifyDataSetChanged()
    }
}