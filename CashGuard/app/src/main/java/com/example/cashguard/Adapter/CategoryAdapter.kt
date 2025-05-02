package com.example.cashguard.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cashguard.R
import com.example.cashguard.data.Category

class CategoryAdapter(
    context: Context,
    private var categories: List<Category>) :
    ArrayAdapter<Category>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    // This method is used to create the view for the dropdown items
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    // This method is used to create the view for the selected item
    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Inflate the custom layout for the spinner item
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)

        // Find the TextView in the inflated layout
        val textView = view.findViewById<TextView>(R.id.text_view)

        // Set the text for the TextView based on the selected item
        getItem(position)?.let { category ->
            textView.text = category.name
        } ?: run {
            textView.text = "Select Category"
        }

        return view
    }

}