package com.example.cashguard.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Activities.CategoryManagerActivity
import com.example.cashguard.Activities.DashboardActivity
import com.example.cashguard.Activities.SearchByDateActivity
import com.example.cashguard.Activities.SettingsActivity
import com.example.cashguard.Adapter.CategoryAdapter
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.Model.TransactionViewModelFactory
import com.example.cashguard.ViewModel.CategoryViewModel
import com.example.cashguard.R
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Category
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private var transactionType: String = "Expense"
    private lateinit var categoryList: List<Category>
    private lateinit var spinnerCategory: Spinner
    private lateinit var adapter: CategoryAdapter
    private lateinit var sessionManager : SessionManager
    private var selectedDate: Date? = null
    private var userId: Int = -1

    private var selectedPhotoUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPhotoUri = uri
        if (uri != null) {
            Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("Activity", "Add Transaction")

        // Initialize ViewModels
        fun setupViewModels() {
            val transactionDao = AppDatabase.getInstance(this).transactionDao()
            val repository = TransactionRepository(transactionDao)
            val factory = TransactionViewModelFactory(repository)

            transactionViewModel = ViewModelProvider(this, factory)
                .get(TransactionViewModel::class.java)
        }

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        spinnerCategory = findViewById(R.id.spinner_category)

        transactionType = intent.getStringExtra("TRANSACTION_TYPE") ?: run {
            showErrorAndFinish("Invalid transaction type")
            return
        }

        // Set the titleText dynamically based on transactionType
        binding.titleText.text = when (transactionType) {
            "Income" -> "Add Income"
            "Expense" -> "Add Expense"
            else -> "Add Transaction"
        }

        // Pass transactionType to populateCategoryList
        populateCategoryList(userId, transactionType)
        setupSubmitButton()
        setupViewModels()

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

        binding.homeIcon.setOnClickListener {

            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            startActivity(intent)
            finish()
        }

        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun populateCategoryList(userId: Int, transactionType: String) {
        lifecycleScope.launch {
            try {

                // Fetch categories based on transaction type
                categoryList = when (transactionType) {
                    "Income" -> categoryViewModel.getIncomeCategories(userId)
                    else -> categoryViewModel.getExpenseCategories(userId)
                }

                // Update spinner
                adapter = CategoryAdapter(this@AddTransactionActivity, categoryList)
                spinnerCategory.adapter = adapter

                if (categoryList.isEmpty()) {
                    showNoCategoriesDialog()
                }

            } catch (e: Exception) {
                Log.e("AddTransactionActivity", "Error fetching categories: ${e.message}")
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                binding.btnDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupSubmitButton() {

        binding.buttonSubmit.setOnClickListener {
            val amountText = binding.editTextAmount.text.toString()
            val note = binding.editTextNote.text.toString()
            val selectedCategory = binding.spinnerCategory.selectedItem as? Category

            // Validation
            when {
                amountText.isBlank() -> {
                    binding.editTextAmount.error = "Amount required"
                    return@setOnClickListener
                }

                selectedCategory == null -> {
                    Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val amount = try {
                amountText.toDouble()
            } catch (e: NumberFormatException) {
                binding.editTextAmount.error = "Invalid amount format"
                return@setOnClickListener
            }

            val transaction = Transaction(
                userId = userId,
                date = selectedDate ?: Date(),
                amount = amount,
                note = note.ifBlank { null },
                type = transactionType,
                photoUri = selectedPhotoUri?.toString(),
                categoryName = selectedCategory?.name ?: run {
                    Toast.makeText(this, "Invalid category selection", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            )

            lifecycleScope.launch {
                try {
                    transactionViewModel.addTransaction(transaction)
                    Toast.makeText(
                        this@AddTransactionActivity,
                        "Transaction saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AddTransactionActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showNoCategoriesDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Categories Found")
            .setMessage("Create $transactionType categories first")
            .setPositiveButton("Create") { _, _ ->
                startActivity(Intent(this, CategoryManagerActivity::class.java))
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
            .show()
    }

}