package com.example.cashguard.Acitivties

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Adapter.CategoryAdapter
import com.example.cashguard.Model.CategoryViewModel
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.data.Category
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var userId: Int = -1
    private var transactionType: String = "Expense"
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Validate intent extras
        userId = intent.getIntExtra("USER_ID", -1).takeIf { it != -1 } ?: run {
            showErrorAndFinish("Invalid user session")
            return
        }

        transactionType = intent.getStringExtra("TRANSACTION_TYPE") ?: run {
            showErrorAndFinish("Invalid transaction type")
            return
        }

        // Initialize ViewModels
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupCategorySpinner()
        setupSubmitButton()
    }

    private fun setupCategorySpinner() {
        // Initialize adapter with empty list
        categoryAdapter = CategoryAdapter(
            this,
            android.R.layout.simple_spinner_item,
            emptyList()
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerAlgorithm.adapter = categoryAdapter

        // Observe categories
        categoryViewModel.getCategoriesByType(userId, transactionType).observe(this) { categories ->
            if (categories.isEmpty()) {
                showNoCategoriesDialog()
            } else {
                categoryAdapter.clear()
                categoryAdapter.addAll(categories)
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupSubmitButton() {
        binding.buttonSubmit.setOnClickListener {
            val amountText = binding.editTextAmount.text.toString()
            val note = binding.editTextNote.text.toString()
            val selectedCategory = binding.spinnerAlgorithm.selectedItem as? Category

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
                    Toast.makeText(this@AddTransactionActivity, "Transaction saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AddTransactionActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showNoCategoriesDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Categories Found")
            .setMessage("Create $transactionType categories first")
            .setPositiveButton("Create") { _, _ ->
                // Navigate to category creation
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
            .show()
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}