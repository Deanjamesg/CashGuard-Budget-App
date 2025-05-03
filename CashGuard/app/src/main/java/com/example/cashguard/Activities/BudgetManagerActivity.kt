package com.example.cashguard.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
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
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIconListeners()

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
            val intent = Intent(this, BudgetBalancesActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    // Function to save the total expenses
    private fun saveTotalExpenses() {
        val budgetDao = AppDatabase.getInstance(this).budgetDao()

        val budgetTotal = binding.tvBudgetTotal.text.toString()

        val totalExpensesInput = budgetTotal

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
        val currentMonth = java.text.SimpleDateFormat("MMM-yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
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
            Toast.makeText(
                this@BudgetManagerActivity,
                "Budget saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fetchAndDisplayCategories() {
        val categoryDao = AppDatabase.getInstance(this).categoryDao()

        lifecycleScope.launch {
            val expenseCategories = withContext(Dispatchers.IO) {
                categoryDao.getExpenseCategoriesByUser(userId)
            }

            val editTextList = mutableListOf<EditText>()

            for (category in expenseCategories) {
                val fragmentView = LayoutInflater.from(this@BudgetManagerActivity)
                    .inflate(R.layout.fragment_category_input, binding.categoryContainer, false)

                fragmentView.findViewById<TextView>(R.id.categoryName).text = category.name
                val budgetInput = fragmentView.findViewById<EditText>(R.id.budgetInput)
                budgetInput.setText(category.budgetAmount?.toInt()?.toString() ?: "0")

                // Add InputFilter to restrict input to whole numbers
                budgetInput.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                    if (source.matches(Regex("\\d*"))) source else ""
                })

                budgetInput.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        updateTotalBudget(editTextList)
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })

                editTextList.add(budgetInput)
                binding.categoryContainer.addView(fragmentView)
            }

            updateTotalBudget(editTextList)
        }
    }

    private fun updateTotalBudget(editTextList: List<EditText>) {
        var totalBudget = 0.0
        for (editText in editTextList) {
            val input = editText.text.toString()
            val value = input.toDoubleOrNull() ?: 0.0
            totalBudget += value
        }
        binding.tvBudgetTotal.text =
            String.format("%.0f", totalBudget) // Display total as a whole number
    }

    // Function to save category budgets
    private fun saveCategoryBudgets() {
        val categoryDao = AppDatabase.getInstance(this).categoryDao()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                for (i in 0 until binding.categoryContainer.childCount) {
                    val categoryView = binding.categoryContainer.getChildAt(i)
                    val categoryName =
                        categoryView.findViewById<TextView>(R.id.categoryName).text.toString()
                    val budgetInput =
                        categoryView.findViewById<EditText>(R.id.budgetInput).text.toString()

                    if (budgetInput.isNotBlank()) {
                        val budgetAmount = budgetInput.toDoubleOrNull()
                        if (budgetAmount != null) {
                            // Fetch the category by name and update its budget amount
                            val category = categoryDao.getCategoriesByUser(userId)
                                .find { it.name == categoryName }
                            if (category != null) {
                                categoryDao.update(category.copy(budgetAmount = budgetAmount))
                            }
                        }
                    }
                }
            }
            Toast.makeText(
                this@BudgetManagerActivity,
                "Category budgets updated successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupIconListeners() {

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    // OLD AND WORKING

    // Function to fetch and display categories
//    private fun fetchAndDisplayCategories() {
//        val categoryDao = AppDatabase.getInstance(this).categoryDao()
//
//        lifecycleScope.launch {
//            val expenseCategories = withContext(Dispatchers.IO) {
//                categoryDao.getExpenseCategoriesByUser(userId) // Fetch only expense categories for the user
//            }
//
//            for (category in expenseCategories) {
//                val fragmentView = LayoutInflater.from(this@BudgetManagerActivity)
//                    .inflate(R.layout.fragment_category_input, binding.categoryContainer, false)
//
//                fragmentView.findViewById<TextView>(R.id.categoryName).text = category.name
//                val budgetInput = fragmentView.findViewById<EditText>(R.id.budgetInput)
//                budgetInput.setText(category.budgetAmount?.toString() ?: "0") // Display budgetAmount or 0 if null
//
//                binding.categoryContainer.addView(fragmentView)
//            }
//        }
//    }


}