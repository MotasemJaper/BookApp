package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.ModelMajor
import com.example.bookapp.activities.adapter.AdapterViewMajors

class FilterMajor : Filter {

    private var filterList: ArrayList<ModelMajor>
    private var adapterMajor: AdapterViewMajors

    constructor(filterList: ArrayList<ModelMajor>, adapterMajor: AdapterViewMajors) : super() {
        this.filterList = filterList
        this.adapterMajor = adapterMajor
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<ModelMajor> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].majorName.uppercase().contains(constraint)) {
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
        adapterMajor.modelMajor = results.values as ArrayList<ModelMajor>
        adapterMajor.notifyDataSetChanged()
    }
}