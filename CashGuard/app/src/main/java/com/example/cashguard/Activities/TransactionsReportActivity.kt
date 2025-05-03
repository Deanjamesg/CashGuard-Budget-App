// Source: Your existing code patterns
// Author: Your name
// Title: Transaction Report Activity Implementation
package com.example.cashguard.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.Model.TransactionViewModelFactory
import com.example.cashguard.R
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ActivityTransactionsReportBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionsReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionsReportBinding
    private lateinit var viewModel: TransactionViewModel
    private var selectedFromDate: Date? = null
    private var selectedToDate: Date? = null
    private var selectedType: String = "Expense"  // Default to match your transaction types

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = TransactionViewModelFactory(
            repository = TransactionRepository(
                AppDatabase.getInstance(this).transactionDao()
            )
        )
        viewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        setupDatePickers()
        setupSpinner()
        setupSearchButton()
        setupBottomNav()
    }

    // Function to set up date pickers for selecting date range
    private fun setupDatePickers() {
        binding.btnFromDate.setOnClickListener { showDatePicker(true) }
        binding.btnToDate.setOnClickListener { showDatePicker(false) }
    }

    // Function to show date picker dialog
    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.apply {
                    set(year, month, day)
                    if (isFromDate) {
                        // Set the time to the start of the day
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        selectedFromDate = time
                        binding.btnFromDate.text = formatDate(time)
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        selectedToDate = time
                        binding.btnToDate.text = formatDate(time)
                    }
                }
            },
            // Set the current date as the default date
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Function to format date to a readable string
    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerType.adapter = adapter
        }


        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedType = parent?.getItemAtPosition(pos).toString()
                Log.d("Report", "Selected type: $selectedType")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Function to set up the search button
    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            if (selectedFromDate == null || selectedToDate == null) {
                showToast("Please select both dates")
                return@setOnClickListener
            }

            // Validate date range
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userId = SessionManager(this@TransactionsReportActivity).getUserId()
                    Log.d("Report", "UserID: $userId, From: $selectedFromDate, To: $selectedToDate")

                    // Fetch transactions from the database
                    val transactions = viewModel.getTransactionsByDateRange(
                        userId,
                        selectedFromDate!!,
                        selectedToDate!!
                    ).filter {
                        it.type.equals(selectedType, ignoreCase = true)
                    }

                    Log.d("Report", "Found ${transactions.size} transactions")
                    showResults(transactions)
                } catch (e: Exception) {
                    showToast("Error loading transactions: ${e.message}")
                    Log.e("TransactionsReport", "Error", e)
                }
            }
        }
    }

    private fun showResults(transactions: List<Transaction>) {
        val container = binding.resultsContainer
        container.removeAllViews()

        if (transactions.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No transactions found"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@TransactionsReportActivity, R.color.white))
                setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
            }
            container.addView(tv)
            return
        }

        // Display each transaction in the results container
        transactions.forEach { transaction ->
            val tv = TextView(this).apply {
                text = "${formatDate(transaction.date)} - ${transaction.categoryName}: R${"%.2f".format(transaction.amount)}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@TransactionsReportActivity, R.color.white))
                setPadding(0, 8.dpToPx(), 0, 8.dpToPx())
            }
            container.addView(tv)
        }
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    // Function to set up bottom navigation bar
    private fun setupBottomNav() {
        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", SessionManager(this@TransactionsReportActivity).getUserId())
            }
            startActivity(intent)
        }

        binding.settingsIcon.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}