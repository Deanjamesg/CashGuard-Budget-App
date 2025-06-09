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

    interface OnBudgetChangedListener {
        fun onBudgetChanged()
    }

    private var listener: OnBudgetChangedListener? = null

    // REPAIRED: Key changed from Int to String to match the categoryId type.
    private val changedBudgets = mutableMapOf<String, Double>()

    inner class BudgetViewHolder(private val binding: ItemBudgetManagerBinding) : RecyclerView.ViewHolder(binding.root) {
        private var textWatcher: TextWatcher? = null

        fun bind(category: Category) {
            binding.categoryName.text = category.name
            binding.budgetInput.removeTextChangedListener(textWatcher)

            if (category.maxGoal != null && category.maxGoal!! > 0) {
                val formattedAmount = String.format(Locale.getDefault(), "%.0f", category.maxGoal)
                binding.budgetInput.setText(formattedAmount)
            } else {
                binding.budgetInput.setText("")
                binding.budgetInput.hint = "R0"
            }

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val newAmount = s.toString().toDoubleOrNull() ?: 0.0
                    // The String categoryId now correctly works as the key.
                    changedBudgets[category.categoryId] = newAmount
                    listener?.onBudgetChanged()
                }
            }
            binding.budgetInput.addTextChangedListener(textWatcher)
        }
    }

    fun setOnBudgetChangedListener(listener: OnBudgetChangedListener) {
        this.listener = listener
    }

    fun calculateNewTotal(): Double {
        var total = 0.0
        currentList.forEach { category ->
            // REPAIRED: The fallback now uses the correct 'maxGoal' field instead of the non-existent 'budgetAmount'.
            val amount = changedBudgets[category.categoryId] ?: (category.maxGoal ?: 0.0)
            total += amount
        }
        return total
    }

    fun getChangedCategories(): List<Category> {
        val updatedCategories = mutableListOf<Category>()
        changedBudgets.forEach { (categoryId, newAmount) ->
            val originalCategory = currentList.find { it.categoryId == categoryId }
            if (originalCategory != null) {
                // REPAIRED: .copy() now updates the correct 'maxGoal' field.
                val updatedCategory = originalCategory.copy(maxGoal = newAmount)
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