package com.example.cashguard.Activities

import android.app.DatePickerDialog
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
    private var selectedType: String = ""

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
    }

    // Set up date pickers for selecting date range
    private fun setupDatePickers() {
        binding.btnFromDate.setOnClickListener { showDatePicker(true) }
        binding.btnToDate.setOnClickListener { showDatePicker(false) }
    }

    // Show date picker dialog
    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.apply {
                    set(year, month, day)
                    // Set the time to the start or end of the day
                    if (isFromDate) {
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
            // Set the initial date to today
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Set up spinner for selecting transaction type
    private fun setupSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerType.adapter = adapter
        }

        // Set the default selection to the first item
        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                selectedType = parent?.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Set up search button to fetch transactions based on selected date range and type
    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            if (selectedFromDate == null || selectedToDate == null) {
                showToast("Please select both dates")
                return@setOnClickListener
            }

            // Check if the selected date range is valid
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // Fetch transactions based on selected date range and type
                    val transactions = viewModel.getTransactionsByDateRange(
                        SessionManager(this@TransactionsReportActivity).getUserId(),
                        selectedFromDate!!,
                        selectedToDate!!
                    ).filter { it.type == selectedType }

                    // Group transactions by category and calculate totals
                    val totals = transactions.groupBy { it.categoryName }
                        .mapValues { (_, transactions) ->
                            transactions.sumOf { it.amount }
                        }

                    //  Display the results
                    showCategoryTotals(totals)
                } catch (e: Exception) {
                    showToast("Error loading transactions: ${e.message}")
                    Log.e("TransactionsReport", "Error", e)
                }
            }
        }
    }

    // Display the category totals in the results container
    private fun showCategoryTotals(totals: Map<String, Double>) {
        val container = binding.resultsContainer
        container.removeAllViews()

        // Check if there are no transactions
        if (totals.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No transactions found"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@TransactionsReportActivity, R.color.white))
                setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
            }
            container.addView(tv)
            return
        }

        // Add category totals
        totals.forEach { (category, amount) ->
            val tv = TextView(this).apply {
                text = "$category: R${"%.2f".format(amount)}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@TransactionsReportActivity, R.color.white))
                setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
            }
            container.addView(tv)
        }

        // Add total summary
        val totalAmount = totals.values.sum()
        val summaryTv = TextView(this).apply {
            text = "Total: R${"%.2f".format(totalAmount)}"
            textSize = 18f
            setTextColor(ContextCompat.getColor(this@TransactionsReportActivity, R.color.glow))
            setPadding(0, 32.dpToPx(), 0, 0)
        }
        container.addView(summaryTv)
    }

    // Format date to a readable string
    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }

    // Show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Extension function to convert dp to px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}