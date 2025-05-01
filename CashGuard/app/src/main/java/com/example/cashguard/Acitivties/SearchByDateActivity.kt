package com.example.cashguard.Acitivties

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.Adapter.TransactionAdapter
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Intent.SessionManager
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.Model.TransactionViewModelFactory
import com.example.cashguard.R
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.databinding.ActivitySearchByDateBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchByDateActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchByDateBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private var selectedFromDate: Date? = null
    private var selectedToDate: Date? = null

    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchByDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Search By Date ID: ${sessionManager.getUserId()}")

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Get user ID from intent
//        userId = intent.getIntExtra("USER_ID", -1)
//        if (userId == -1) finish()

        // Initialize components
        setupViewModel()
        setupRecyclerView()
        setupDatePickers()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList())
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@SearchByDateActivity)
            adapter = transactionAdapter
        }
    }

    private fun setupDatePickers() {
        binding.btnFromDate.setOnClickListener { showDatePicker(true) }
        binding.btnToDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.apply {
                    set(year, month, day)
                    if (isFromDate) {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        selectedFromDate = time
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        selectedToDate = time
                    }
                }
                loadTransactions()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupViewModel() {
        // Fixed: Proper initialization flow
        val transactionDao = AppDatabase.getInstance(this).transactionDao()
        val repository = TransactionRepository(transactionDao)
        val factory = TransactionViewModelFactory(repository)

        transactionViewModel = ViewModelProvider(this, factory)
            .get(TransactionViewModel::class.java)
    }

    private fun loadTransactions() {
        if (selectedFromDate == null || selectedToDate == null) return

        lifecycleScope.launch {
            try {
                val transactions = transactionViewModel.getTransactionsByDateRange(
                    userId,
                    selectedFromDate!!,
                    selectedToDate!!
                )
                for (transaction in transactions) {
                    Log.d("Transaction", transaction.toString())
                }
                transactionAdapter.updateData(transactions)
                updateDateRangeText()
            } catch (e: Exception) {
                Toast.makeText(
                    this@SearchByDateActivity,
                    "Error loading transactions: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateDateRangeText() {
        val from = SimpleDateFormat("dd MMM", Locale.getDefault()).format(selectedFromDate!!)
        val to = SimpleDateFormat("dd MMM", Locale.getDefault()).format(selectedToDate!!)
        binding.tvDateRange.text = "$from – $to"
    }
}