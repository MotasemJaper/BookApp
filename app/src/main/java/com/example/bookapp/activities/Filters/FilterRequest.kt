package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.UserDashBord
import com.example.bookapp.activities.adapter.AdapterViewRequest

class FilterRequest : Filter {

    private var filterList: ArrayList<UserDashBord>
    private var adapterFav: AdapterViewRequest

    constructor(filterList: ArrayList<UserDashBord>, AdapterFav: AdapterViewRequest) : super() {
        this.filterList = filterList
        this.adapterFav = AdapterFav
    }

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint = constraint
        val results = Filter.FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<UserDashBord> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].name.uppercase().contains(constraint)) {
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
        adapterFav.filterList = results.values as ArrayList<UserDashBord>
        adapterFav.notifyDataSetChanged()
    }
}