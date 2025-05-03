package com.example.cashguard.Activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Dao.BudgetDao
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.data.Budget
import com.example.cashguard.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BudgetCalculatorActivity : AppCompatActivity() {

    private lateinit var budgetDao: BudgetDao
    private var currentBudget: Budget? = null
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_calculator)

        // Initialises budgetDao to make use of it in the class
        val database = AppDatabase.getInstance(this)
        budgetDao = database.budgetDao()

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()

        val savingsPercentageInput = findViewById<EditText>(R.id.savingsPercentageInput)
        val incomeInput = findViewById<EditText>(R.id.incomeAmount)
        val budgetAmountText = findViewById<TextView>(R.id.budgetAmount)
        val savingsAmountText = findViewById<TextView>(R.id.savingsAmount)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Calculate savings and budget dynamically as the user inputs numbers
        val calculateValues = {
            val income = incomeInput.text.toString().replace("R", "").toDoubleOrNull() ?: 0.0
            val percentage = savingsPercentageInput.text.toString().replace("%", "").toDoubleOrNull() ?: 0.0 //https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/replace.html
            val savings = income * (percentage / 100)
            val budget = income - savings

            savingsAmountText.text = "R${"%.2f".format(savings)}" //formatted using this method https://discuss.kotlinlang.org/t/format-a-double-to-fixed-decimal-length/20074/7
            budgetAmountText.text = "R${"%.2f".format(budget)}"
        }

        incomeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateValues()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        savingsPercentageInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateValues()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle the submit button click
        submitButton.setOnClickListener {
            val income = incomeInput.text.toString().replace("R", "").toDoubleOrNull()
            val percentage = savingsPercentageInput.text.toString().replace("%", "").toDoubleOrNull()

            if (income != null && percentage != null) {
                val savings = income * (percentage / 100)
                val newBudgetAmount = income - savings

                lifecycleScope.launch {
                    try {
                        // Gets the current month dynamically in the format "MMM-yyyy"
                        val month = SimpleDateFormat("MMM-yyyy", Locale.getDefault()).format(Date())

                        currentBudget = withContext(Dispatchers.IO) {
                            budgetDao.getBudgetByMonth(userId, month)
                        }

                        if (currentBudget != null) {
                            val updatedBudget = currentBudget!!.copy(budgetAmount = newBudgetAmount)
                            withContext(Dispatchers.IO) {
                                budgetDao.update(updatedBudget)
                            }
                            Toast.makeText(this@BudgetCalculatorActivity, "Budget updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BudgetCalculatorActivity, "No existing budget found for the month", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@BudgetCalculatorActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter valid income and percentage", Toast.LENGTH_SHORT).show()
            }
        }
    }
}