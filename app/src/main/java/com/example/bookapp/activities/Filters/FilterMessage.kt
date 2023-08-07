package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.Message
import com.example.bookapp.activities.adapter.AdapterForAllMessage

class FilterMessage : Filter {

    private var filterList: ArrayList<Message>
    private var adapterMessage: AdapterForAllMessage

    constructor(filterList: ArrayList<Message>, adapterMajor: AdapterForAllMessage) : super() {
        this.filterList = filterList
        this.adapterMessage = adapterMajor
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<Message> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].email.uppercase().contains(constraint)) {
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
        adapterMessage.filterList = results.values as ArrayList<Message>
        adapterMessage.notifyDataSetChanged()
    }
}