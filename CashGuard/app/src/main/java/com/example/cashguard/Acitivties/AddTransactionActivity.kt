package com.example.cashguard.Acitivties

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Adapter.CategoryAdapter
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Intent.SessionManager
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.Model.TransactionViewModelFactory
import com.example.cashguard.ViewModel.CategoryViewModel
import com.example.cashguard.R
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Category
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private var transactionType: String = "Expense"
    private lateinit var categoryList: List<Category>
    private lateinit var spinnerCat: Spinner
    private lateinit var adapter: CategoryAdapter
    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Validate intent extras
        userId = intent.getIntExtra("USER_ID", -1).takeIf { it != -1 } ?: run {
            Toast.makeText(this, "Invalid category {userId} selection", Toast.LENGTH_SHORT).show()
            showErrorAndFinish("Invalid user session")
            return
        }

        // Will be set with Cookies / User Session
        userId = intent.getIntExtra("USER_ID", -1).takeIf { it != -1 } ?: run {
            showErrorAndFinish("Invalid user session")
            return
        }

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Add Transaction ID: ${sessionManager.getUserId()}")

        // Initialize ViewModels
         fun setupViewModels() {
            val transactionDao = AppDatabase.getInstance(this).transactionDao()
            val repository = TransactionRepository(transactionDao)
            val factory = TransactionViewModelFactory(repository)

            transactionViewModel = ViewModelProvider(this, factory)
                .get(TransactionViewModel::class.java)
        }

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        spinnerCat = findViewById(R.id.spinner_category)

        transactionType = intent.getStringExtra("TRANSACTION_TYPE") ?: run {
            showErrorAndFinish("Invalid transaction type")
            return
        }

        populateCategoryList(userId)
        setupSubmitButton()
        setupViewModels()

        binding.homeIcon.setOnClickListener {
            // Create intent to return to BudgetOverviewActivity
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish() // Close current activity
        }

        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", userId) // Pass user ID to search activity
            }
            startActivity(intent)
        }
    }

    private fun populateCategoryList(userId: Int) {

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        lifecycleScope.launch {
            try {
                categoryList = categoryViewModel.getExpenseCategories(userId)

                // Log or use the populated list
                for (category in categoryList) {
                    Log.d("AddTransactionActivity", "Category: ${category.name}")
                }
                adapter = CategoryAdapter(this@AddTransactionActivity, categoryList)
                spinnerCat.adapter = adapter

            } catch (e: Exception) {
                Log.e("AddTransactionActivity", "Error fetching categories: ${e.message}")
            }
        }
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
                date = Date(),
                amount = amount,
                note = note.ifBlank { null },
                type = transactionType,
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

//    private fun showNoCategoriesDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("No Categories Found")
//            .setMessage("Create $transactionType categories first")
//            .setPositiveButton("Create") { _, _ ->
//                // Navigate to category creation
//            }
//            .setNegativeButton("Cancel") { _, _ -> finish() }
//            .show()
//    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}