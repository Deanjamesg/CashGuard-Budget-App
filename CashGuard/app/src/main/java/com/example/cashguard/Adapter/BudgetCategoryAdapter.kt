package com.example.cashguard.Adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.ViewModel.BudgetInfo
import com.example.cashguard.ViewModel.BudgetProgressBarView
import android.util.Log



class BudgetCategoryAdapter : ListAdapter<BudgetInfo, BudgetCategoryAdapter.BudgetViewHolder>(BudgetInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        // Use the correct path for BudgetProgressBarView based on where you placed it
        val view = BudgetProgressBarView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // --- ViewHolder accepts BudgetInfo ---
    class BudgetViewHolder(private val view: BudgetProgressBarView) : RecyclerView.ViewHolder(view) {
        fun bind(budgetInfo: BudgetInfo) { // Parameter type is now BudgetInfo
            view.setBudgetInfo(budgetInfo) // Call the updated method in the view (we'll rename it next)
        }
    }

    // --- DiffUtil compares BudgetInfo ---
    class BudgetInfoDiffCallback : DiffUtil.ItemCallback<BudgetInfo>() {
        override fun areItemsTheSame(oldItem: BudgetInfo, newItem: BudgetInfo): Boolean {
            // Compare based on a unique ID
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: BudgetInfo, newItem: BudgetInfo): Boolean {
            // Compare all relevant fields
            return oldItem == newItem
        }
    }
}