package com.example.cashguard.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CategoryViewModel::class.java]

        setupObservers()
        initializeCategories()
        setupClickListeners()
        setupBottomNav()
    }

    // Function to fetch and display categories
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

    // Function to refresh the display of categories
    private fun refreshCategoryDisplay(categories: List<Category>) {
        val incomeContainer: LinearLayout = findViewById(R.id.incomeCategoriesContainer)
        val expenseContainer: LinearLayout = findViewById(R.id.expenseCategoriesContainer)

        incomeContainer.removeAllViews()
        expenseContainer.removeAllViews()

        // Populate the income and expense categories
        categories.forEach { category ->
            val container = when (category.type) {
                "Income" -> incomeContainer
                "Expense" -> expenseContainer
                else -> null
            }

            // Create a button for each category
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
                    // Set the click listener to open the category editor
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

//OLD
//    private fun createCategoryButton(category: Category): Button {
//        return Button(this).apply {
//            text = category.name
//            setTextColor(Color.WHITE)
//            //background = ContextCompat.getDrawable(context, R.drawable.button_background)
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 16.dpToPx())
//            }
//        }
//    }

    //navigation
    private fun setupClickListeners() {
        findViewById<Button>(R.id.addButton).setOnClickListener { showAddDialog() }
        findViewById<Button>(R.id.removeButton).setOnClickListener { showRemoveDialog() }
        findViewById<Button>(R.id.submitBudget).setOnClickListener {
            startActivity(Intent(this, BudgetManagerActivity::class.java))
        }
    }

    // Function to show the add category dialog
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

    // Function to handle category creation
    private fun handleCategoryCreation(nameET: EditText, typeRG: RadioGroup) {
        val name = nameET.text.toString().trim()
        val type = when (typeRG.checkedRadioButtonId) {
            // uses radio button ids to determine type
            R.id.rbExpense -> "Expense"
            R.id.rbIncome -> "Income"
            else -> null
        }

        // Validate input
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

    // Function to show the remove category dialog
    private fun showRemoveDialog() {
        val removable = viewModel.getRemovableCategories()
        when {
            removable.isEmpty() -> showError("No removable categories")
            else -> showRemovalSelectionDialog(removable)
        }
    }

    // Function to show the removal selection dialog
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

    // Function to set up bottom navigation bar
    private fun setupBottomNav() {
        findViewById<ImageButton>(R.id.homeIcon).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", sessionManager.getUserId())
            }
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.settingsIcon).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

}