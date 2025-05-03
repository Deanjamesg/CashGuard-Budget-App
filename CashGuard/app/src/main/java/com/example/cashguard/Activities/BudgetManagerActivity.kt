package com.example.cashguard.Activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.R
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.data.Budget
import com.example.cashguard.databinding.ActivityBudgetManagerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetManagerBinding
    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialises the databases
        val database = AppDatabase.getInstance(this)
        var budgetDao = database.budgetDao()

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()

        // Fetch categories and populate the ScrollView
        fetchAndDisplayCategories()

        binding.submitButton.setOnClickListener {
            saveTotalExpenses()
            saveCategoryBudgets()
        }
    }
    // Function to save the total expenses
    private fun saveTotalExpenses() {
        val budgetDao = AppDatabase.getInstance(this).budgetDao()
        val totalExpensesInput = binding.budgetAmountEditText.text.toString()

        if (totalExpensesInput.isBlank()) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val totalExpenses = totalExpensesInput.toDoubleOrNull()
        if (totalExpenses == null) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show()
            return
        }
        // Gets the current month in the format "MMM-yyyy"
        val currentMonth = java.text.SimpleDateFormat("MMM-yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val existingBudget = budgetDao.getBudgetByMonth(userId, currentMonth)

                if (existingBudget != null) {
                    // Update the existing budget
                    val updatedBudget = existingBudget.copy(budgetAmount = totalExpenses)
                    budgetDao.update(updatedBudget)
                } else {
                    // Insert a new budget
                    val newBudget = Budget(
                        userId = 1,
                        financialMonth = currentMonth,
                        budgetAmount = totalExpenses
                    )
                    budgetDao.insert(newBudget)
                }
            }
            Toast.makeText(this@BudgetManagerActivity, "Budget saved successfully", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to fetch and display categories
    private fun fetchAndDisplayCategories() {
        val categoryDao = AppDatabase.getInstance(this).categoryDao()

        lifecycleScope.launch {
            val expenseCategories = withContext(Dispatchers.IO) {
                categoryDao.getExpenseCategoriesByUser(userId) // Fetch only expense categories for the user
            }

            for (category in expenseCategories) {
                val fragmentView = LayoutInflater.from(this@BudgetManagerActivity)
                    .inflate(R.layout.fragment_category_input, binding.categoryContainer, false)

                fragmentView.findViewById<TextView>(R.id.categoryName).text = category.name
                val budgetInput = fragmentView.findViewById<EditText>(R.id.budgetInput)
                budgetInput.setText(category.budgetAmount?.toString() ?: "0") // Display budgetAmount or 0 if null

                binding.categoryContainer.addView(fragmentView)
            }
        }
    }

    // Function to save category budgets
    private fun saveCategoryBudgets() {
        val categoryDao = AppDatabase.getInstance(this).categoryDao()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                for (i in 0 until binding.categoryContainer.childCount) {
                    val categoryView = binding.categoryContainer.getChildAt(i)
                    val categoryName = categoryView.findViewById<TextView>(R.id.categoryName).text.toString()
                    val budgetInput = categoryView.findViewById<EditText>(R.id.budgetInput).text.toString()

                    if (budgetInput.isNotBlank()) {
                        val budgetAmount = budgetInput.toDoubleOrNull()
                        if (budgetAmount != null) {
                            // Fetch the category by name and update its budget amount
                            val category = categoryDao.getCategoriesByUser(userId).find { it.name == categoryName }
                            if (category != null) {
                                categoryDao.update(category.copy(budgetAmount = budgetAmount))
                            }
                        }
                    }
                }
            }
            Toast.makeText(this@BudgetManagerActivity, "Category budgets updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
}