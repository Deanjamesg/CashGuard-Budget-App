package com.example.cashguard.ViewModel

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.cashguard.R
import com.example.cashguard.databinding.ViewBudgetProgressBarBinding
import java.text.NumberFormat
import java.util.Locale
import com.example.cashguard.ViewModel.BudgetInfo

import android.util.Log
import android.view.View


class BudgetProgressBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewBudgetProgressBarBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = ViewBudgetProgressBarBinding.inflate(inflater, this)
        // Set minHeight using the dimension resource from main project
        minimumHeight = try {
            resources.getDimensionPixelSize(R.dimen.budget_item_min_height)
        } catch (e: Exception) {
            (72 * resources.displayMetrics.density).toInt() // Fallback
        }
    }


    fun setBudgetInfo(budgetInfo: BudgetInfo) {
        // Log when the method starts and the data it received
        Log.d(
            "BudgetProgressBarView",
            "setBudgetInfo called for CategoryID: ${budgetInfo.categoryId}"
        )
        Log.d(
            "BudgetProgressBarView",
            "Data Received -> Name: ${budgetInfo.name}, Budget: ${budgetInfo.budgetAmount}, Spent: ${budgetInfo.spentAmount}, Color: ${budgetInfo.color}"
        )

        with(binding) { // Use view binding

            // 1. Set Category Name
            categoryName.text = budgetInfo.name
            Log.d("BudgetProgressBarView", "Set categoryName text to: ${budgetInfo.name}")

            // 2. Calculate Percentage
            val percentage = if (budgetInfo.budgetAmount > 0) {
                (budgetInfo.spentAmount / budgetInfo.budgetAmount * 100).toInt()
            } else {
                0 // Avoid division by zero if budget is 0
            }.coerceIn(0, 100) // Ensure percentage is between 0 and 100
            Log.d("BudgetProgressBarView", "Calculated Percentage: $percentage")

            // 3. Set Progress Bar Value
            progressBar.progress = percentage
            Log.d("BudgetProgressBarView", "Set progressBar progress to: $percentage")

            // 4. Apply Color Tint to Progress Bar
            progressBar.progressTintList = ColorStateList.valueOf(budgetInfo.color)
            Log.d(
                "BudgetProgressBarView",
                "Applied progressTintList with ColorInt: ${budgetInfo.color}"
            )

            // 5. Format Currency Text
            try {
                val currencyFormat =
                    NumberFormat.getCurrencyInstance(Locale("en", "ZA")) // Use your Locale
                val formattedSpent = currencyFormat.format(budgetInfo.spentAmount)
                val formattedBudget = currencyFormat.format(budgetInfo.budgetAmount)
                val amountString = context.getString(
                    R.string.budget_amount_format,
                    formattedSpent,
                    formattedBudget
                )

                amountText.text = amountString // Set amount text
                Log.d("BudgetProgressBarView", "Set amountText to: $amountString")

            } catch (e: Exception) {
                Log.e("BudgetProgressBarView", "Error formatting currency: ${e.message}")
                amountText.text = "Error" // Show error in UI
            }

            // 6. Format and Set Percentage Text
            try {
                val percentageString =
                    context.getString(R.string.budget_percentage_format, percentage)
                percentageText.text = percentageString // Set percentage text
                Log.d("BudgetProgressBarView", "Set percentageText to: $percentageString")
            } catch (e: Exception) {
                Log.e("BudgetProgressBarView", "Error formatting percentage string: ${e.message}")
                percentageText.text = "ERR%" // Show error in UI
            }

            // 7. Ensure progress bar is visible if it should be
            // (ProgressBar visibility might already be handled by its default state or parent layouts)
            progressBar.visibility = View.VISIBLE // Ensure it's visible
        }
    }
}