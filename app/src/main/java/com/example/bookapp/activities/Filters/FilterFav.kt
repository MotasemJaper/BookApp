package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.ModelFav
import com.example.bookapp.activities.adapter.AdapterFav

class FilterFav : Filter {

    private var filterList: ArrayList<ModelFav>
    private var adapterFav: AdapterFav

    constructor(filterList: ArrayList<ModelFav>, AdapterFav: AdapterFav) : super() {
        this.filterList = filterList
        this.adapterFav = AdapterFav
    }

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint = constraint
        val results = Filter.FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<ModelFav> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].nameBook.uppercase().contains(constraint)) {
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
        adapterFav.listFav = results.values as ArrayList<ModelFav>
        adapterFav.notifyDataSetChanged()
    }
}