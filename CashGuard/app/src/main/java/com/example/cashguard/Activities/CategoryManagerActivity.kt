package com.example.cashguard.Activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.R
import com.example.cashguard.ViewModel.CategoryViewModel
import com.example.cashguard.data.Category

class CategoryManagerActivity : AppCompatActivity() {
    private lateinit var viewModel: CategoryViewModel
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_manager)

        // Initialize session and user ID
        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId().takeIf { it != -1 } ?: run {
            finish()
            return
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CategoryViewModel::class.java]

        setupObservers()
        initializeCategories()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.categories.observe(this) { categories ->
            categories?.let { refreshCategoryDisplay(it) } ?: showError("Failed to load categories")
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let { showError(it) }
        }
    }

    private fun initializeCategories() {
        lifecycleScope.launchWhenStarted {
            viewModel.initializeUserCategories(userId)
        }
    }

    private fun refreshCategoryDisplay(categories: List<Category>) {
        val incomeContainer: LinearLayout = findViewById(R.id.incomeCategoriesContainer)
        val expenseContainer: LinearLayout = findViewById(R.id.expenseCategoriesContainer)

        incomeContainer.removeAllViews()
        expenseContainer.removeAllViews()

        categories.forEach { category ->
            val container = when (category.type) {
                "Income" -> incomeContainer
                "Expense" -> expenseContainer
                else -> null
            }

            container?.let {
                Button(this).apply {
                    text = category.name
                    setTextColor(Color.WHITE)
                    background = ContextCompat.getDrawable(
                        context,
                        if (viewModel.isDefaultCategory(category)) {
                            R.drawable.bg_default_category
                        } else {
                            if (category.type == "Income") R.drawable.bg_income_category
                            else R.drawable.bg_expense_category
                        }
                    )
                    isEnabled = !viewModel.isDefaultCategory(category)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 16.dpToPx())
                    }
                    it.addView(this)
                }
            }
        }
    }


    private fun createCategoryButton(category: Category): Button {
        return Button(this).apply {
            text = category.name
            setTextColor(Color.WHITE)
            //background = ContextCompat.getDrawable(context, R.drawable.button_background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dpToPx())
            }
        }
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.addButton).setOnClickListener { showAddDialog() }
        findViewById<Button>(R.id.removeButton).setOnClickListener { showRemoveDialog() }
        findViewById<Button>(R.id.submitBudget).setOnClickListener { finish() }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.activity_category_editor, null)
        val nameET = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val typeRG = dialogView.findViewById<RadioGroup>(R.id.rgType)

        AlertDialog.Builder(this)
            .setTitle("Add Category")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                handleCategoryCreation(nameET, typeRG)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun handleCategoryCreation(nameET: EditText, typeRG: RadioGroup) {
        val name = nameET.text.toString().trim()
        val type = when (typeRG.checkedRadioButtonId) {
            R.id.rbExpense -> "Expense"
            R.id.rbIncome -> "Income"
            else -> null
        }

        when {
            name.isEmpty() || type == null ->
                showError(if (name.isEmpty()) "Name required" else "Type required")
            else -> viewModel.addCategory(
                Category(
                    userId = userId,
                    name = name,
                    type = type!!
                )
            )
        }
    }

    private fun showRemoveDialog() {
        val removable = viewModel.getRemovableCategories()
        when {
            removable.isEmpty() -> showError("No removable categories")
            else -> showRemovalSelectionDialog(removable)
        }
    }

    private fun showRemovalSelectionDialog(categories: List<Category>) {
        AlertDialog.Builder(this)
            .setTitle("Remove Category")
            .setItems(categories.map { it.name }.toTypedArray()) { _, index ->
                categories.getOrNull(index)?.let { viewModel.deleteCategory(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}