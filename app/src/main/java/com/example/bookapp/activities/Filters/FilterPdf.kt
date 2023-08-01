package com.example.bookapp.activities.Filters

import android.widget.Filter
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.adapter.AdapterPdfView

class FilterPdf : Filter {

    private var filterList: ArrayList<ModelPdf>
    private var adapterPdf: AdapterPdfView

    constructor(filterList: ArrayList<ModelPdf>, adapterPdfView: AdapterPdfView) : super() {
        this.filterList = filterList
        this.adapterPdf = adapterPdfView
    }

    override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
        var constraint = constraint
        val results = Filter.FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filterModel: ArrayList<ModelPdf> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].nameCourse.uppercase().contains(constraint)) {
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
        adapterPdf.modelPdfList = results.values as ArrayList<ModelPdf>
        adapterPdf.notifyDataSetChanged()
    }
}