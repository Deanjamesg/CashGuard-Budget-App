package com.example.cashguard.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.R
import com.example.cashguard.data.ProgressBar
import com.example.cashguard.databinding.ItemBudgetProgressBinding
import java.text.NumberFormat
import java.util.Locale

class BudgetBalanceAdapter : ListAdapter<ProgressBar, BudgetBalanceAdapter.ProgressBarViewHolder>(ProgressBarDiffCallback()) {

    inner class ProgressBarViewHolder(private val binding: ItemBudgetProgressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(progressData: ProgressBar) {
            binding.categoryName.text = progressData.categoryName

            val expense = progressData.expenseAmount
            val budget = progressData.budgetAmount

            val percentage = if (budget > 0) (expense / budget) * 100 else 0.0

            binding.percentageText.text = String.format(Locale.getDefault(), "%.0f%%", percentage)
            binding.progressBar.progress = percentage.toInt().coerceAtMost(100)

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            val formattedExpense = currencyFormat.format(expense)
            val formattedBudget = currencyFormat.format(budget)
            binding.amountText.text = "$formattedExpense / $formattedBudget"

            val context = binding.root.context
            val percentageColor: Int
            val progressColor: Int

            if (percentage > 100) {

                val redColor = ContextCompat.getColor(context, R.color.red)

                val amountOver = expense - budget
                binding.amountOverText.text = currencyFormat.format(amountOver)
                binding.amountOverText.setTextColor(redColor)
                binding.amountOverText.visibility = View.VISIBLE

                // If over budget, set colors to red
                percentageColor = redColor
                progressColor = redColor
            } else {
                percentageColor = ContextCompat.getColor(context, R.color.glow)
                progressColor = ContextCompat.getColor(context, R.color.glow)
            }

            binding.percentageText.setTextColor(percentageColor)

            binding.progressBar.progressTintList = ColorStateList.valueOf(progressColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressBarViewHolder {
        val binding = ItemBudgetProgressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProgressBarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgressBarViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ProgressBarDiffCallback : DiffUtil.ItemCallback<ProgressBar>() {
    override fun areItemsTheSame(oldItem: ProgressBar, newItem: ProgressBar): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }

    override fun areContentsTheSame(oldItem: ProgressBar, newItem: ProgressBar): Boolean {
        return oldItem == newItem
    }
}