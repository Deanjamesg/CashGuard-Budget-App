package com.example.cashguard.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.databinding.ItemCategoryManagerBinding // Use your item layout binding

class CategoryManagerAdapter(
    private val onDeleteClicked: (CategoryItem) -> Unit
) : ListAdapter<CategoryItem, CategoryManagerAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryManagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, onDeleteClicked)
    }

    class CategoryViewHolder(private val binding: ItemCategoryManagerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryItem, onDeleteClicked: (CategoryItem) -> Unit) {
            binding.itemText.text = category.name
            binding.btnDeleteCategory.setOnClickListener {
                onDeleteClicked(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}