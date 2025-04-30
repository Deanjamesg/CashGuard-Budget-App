package com.example.cashguard.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.cashguard.data.Category

class CategoryAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private var categories: List<Category>) : ArrayAdapter<Category>(context, layoutResource, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)

        getItem(position)?.let { category ->
            textView.text = category.name
        } ?: run {
            textView.text = "Select Category"
        }

        return view
    }

    fun updateData(newCategories: List<Category>) {
        clear()
        addAll(newCategories)
        notifyDataSetChanged()
    }
}