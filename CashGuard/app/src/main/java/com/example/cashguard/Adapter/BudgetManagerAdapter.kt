package com.example.cashguard.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.data.Category
import com.example.cashguard.databinding.ItemBudgetManagerBinding
import java.util.Locale

class BudgetManagerAdapter : ListAdapter<Category, BudgetManagerAdapter.BudgetViewHolder>(CategoryDiffCallback()) {

    private val changedMinGoals = mutableMapOf<String, Double>()
    private val changedMaxGoals = mutableMapOf<String, Double>()

    inner class BudgetViewHolder(private val binding: ItemBudgetManagerBinding) : RecyclerView.ViewHolder(binding.root) {
        private var minTextWatcher: TextWatcher? = null
        private var maxTextWatcher: TextWatcher? = null

        fun bind(category: Category) {
            binding.categoryName.text = category.name

            binding.minInput.removeTextChangedListener(minTextWatcher)
            binding.maxInput.removeTextChangedListener(maxTextWatcher)

            if (category.minGoal != null && category.minGoal!! > 0) {
                binding.minInput.setText(String.format(Locale.getDefault(), "%.0f", category.minGoal))
            } else {
                binding.minInput.setText("")
                binding.minInput.hint = "R0"
            }

            if (category.maxGoal != null && category.maxGoal!! > 0) {
                binding.maxInput.setText(String.format(Locale.getDefault(), "%.0f", category.maxGoal))
            } else {
                binding.maxInput.setText("")
                binding.maxInput.hint = "R0"
            }

            minTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val newAmount = s.toString().toDoubleOrNull() ?: 0.0
                    changedMinGoals[category.categoryId] = newAmount
                }
            }

            maxTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val newAmount = s.toString().toDoubleOrNull() ?: 0.0
                    changedMaxGoals[category.categoryId] = newAmount
                }
            }

            binding.minInput.addTextChangedListener(minTextWatcher)
            binding.maxInput.addTextChangedListener(maxTextWatcher)
        }
    }

    fun calculateNewTotal(): Double {
        var total = 0.0
        currentList.forEach { category ->
            val amount = changedMaxGoals[category.categoryId] ?: (category.maxGoal ?: 0.0)
            total += amount
        }
        return total
    }

    fun getChangedCategories(): List<Category> {
        val updatedCategories = mutableListOf<Category>()
        val allChangedIds = (changedMinGoals.keys + changedMaxGoals.keys).distinct()

        allChangedIds.forEach { categoryId ->
            val originalCategory = currentList.find { it.categoryId == categoryId }
            if (originalCategory != null) {
                val updatedCategory = originalCategory.copy(
                    minGoal = changedMinGoals[categoryId] ?: originalCategory.minGoal,
                    maxGoal = changedMaxGoals[categoryId] ?: originalCategory.maxGoal
                )
                updatedCategories.add(updatedCategory)
            }
        }
        return updatedCategories
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetManagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}