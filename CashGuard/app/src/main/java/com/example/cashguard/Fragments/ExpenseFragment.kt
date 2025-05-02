package com.example.cashguard.Fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope // Import lifecycleScope
import com.example.cashguard.Activities.AddTransactionActivity
import com.example.cashguard.Activities.CategoryManagerActivity
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.ViewModel.CategoryViewModel
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.data.Category
import com.example.cashguard.data.Transaction
import com.example.cashguard.R
import com.example.cashguard.databinding.FragmentExpenseBinding
import kotlinx.coroutines.Dispatchers
// Removed GlobalScope import, prefer lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class ExpenseFragment : Fragment() {
    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

    // ViewModels obtained from Activity scope
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val categoryViewModel: CategoryViewModel by activityViewModels()

    // UI References and User Info
    private lateinit var categoryListContainer: LinearLayout
    private var userId: Int = -1

    // Utilities
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA")) // Adjust locale if needed


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        // Initialize categoryListContainer here using the binding
        categoryListContainer = binding.expenseCategoryListContainer
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = sharedViewModel.userId
        if (userId == -1) {
            Log.e("ExpenseFragment", "User ID is invalid!")
            // Consider showing an error message to the user
            return
        }

        setupClickListeners()
        observeData() // Start observing data
    }

    override fun onResume() {
        super.onResume()
        // Trigger data refresh when fragment resumes
        Log.d("ExpenseFragment", "onResume - Triggering category load for potential refresh")
        if (userId != -1) {
            // Reloading categories ensures the observer runs again
            categoryViewModel.loadCategories(userId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }


    private fun setupClickListeners() {
        binding.categoryManager.setOnClickListener {
            navigateToCategoryManager()
        }
        binding.buttonAddExpense.setOnClickListener {
            navigateToAddTransaction("Expense")
        }
        binding.buttonAddIncome.setOnClickListener {
            navigateToAddTransaction("Income")
        }

    }

    private fun navigateToCategoryManager() {
        val intent = Intent(requireContext(), CategoryManagerActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun navigateToAddTransaction(type: String) {
        val intent = Intent(requireContext(), AddTransactionActivity::class.java).apply {
            putExtra("USER_ID", userId)
            putExtra("TRANSACTION_TYPE", type)
        }
        startActivity(intent)
    }


    private fun observeData() {
        // Observe categories from the ViewModel
        categoryViewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            Log.d("ExpenseFragment", "Observer triggered - Categories count: ${categories?.size ?: "null"}")
            if (categories != null) {
                // If categories are available, fetch transactions and process
                fetchAndProcessExpenses(categories)
            } else {
                // If categories are null, show empty state immediately
                Log.w("ExpenseFragment", "Categories list is null, updating UI with empty state.")
                updateExpenseProgressBars(emptyList(), 0.0, emptyMap())
            }
        })


    }

    private fun fetchAndProcessExpenses(categories: List<Category>) {
        Log.d("ExpenseFragment", "fetchAndProcessExpenses - Starting data fetch")
        // Use lifecycleScope tied to the fragment's view lifecycle
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val veryStartDate = Date(0) // Start of epoch
            val veryEndDate = Date()   // Current time
            // Fetch transactions - **Replace with optimized query if possible**
            val allTransactions = try {
                Log.d("ExpenseFragment", "Fetching transactions for user $userId")
                transactionViewModel.getTransactionsByDateRange(userId, veryStartDate, veryEndDate)
            } catch (e: Exception) {
                Log.e("ExpenseFragment", "Error fetching transactions", e)
                listOf<Transaction>() // Return empty list on error
            }
            Log.d("ExpenseFragment", "fetchAndProcessExpenses - Fetched ${allTransactions.size} transactions. Processing...")
            // Process the fetched data
            processExpenses(allTransactions, categories)
        }
    }

    private suspend fun processExpenses(allTransactions: List<Transaction>, categories: List<Category>) {
        // Filter, group, calculate totals
        val expenseTransactions = allTransactions.filter { it.type == "Expense" }
        Log.d("ExpenseFragment", "processExpenses - Filtered ${expenseTransactions.size} expense transactions.")

        val expenseByCategory = expenseTransactions
            .groupBy { it.categoryName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
        Log.d("ExpenseFragment", "processExpenses - Grouped into ${expenseByCategory.size} categories.")

        val totalExpenses = expenseByCategory.sumOf { it.second }
        Log.d("ExpenseFragment", "processExpenses - Total Expenses: $totalExpenses")

        val categoryColorMap = categories
            .filter { it.type == "Expense" }
            .associateBy({ it.name }, { it.color })

        // Switch to Main thread to update UI
        withContext(Dispatchers.Main) {
            Log.d("ExpenseFragment", "processExpenses - Updating UI on Main thread.")
            // Update total text safely
            binding.totalExpensesText?.text = currencyFormatter.format(totalExpenses)
            // Update the list of progress bars
            updateExpenseProgressBars(expenseByCategory, totalExpenses, categoryColorMap)
        }
    }


    private fun updateExpenseProgressBars(
        expenses: List<Pair<String, Double>>, // List of Pair(CategoryName, Amount)
        totalExpenses: Double,
        colorMap: Map<String, Int>
    ) {
        val safeContext = context ?: run {
            Log.w("ExpenseFragment", "updateExpenseProgressBars - Context is null, cannot update UI.")
            return
        }
        // Ensure container is not null (should be initialized in onCreateView)
        val container = _binding?.expenseCategoryListContainer ?: run {
            Log.e("ExpenseFragment", "updateExpenseProgressBars - LinearLayout container is null!")
            return
        }
        container.removeAllViews() // Clear previous items

        Log.d("ExpenseFragment", "updateExpenseProgressBars - Updating UI; list size: ${expenses.size}")

        if (expenses.isEmpty()) {
            // Display "No expenses" message
            val noDataText = TextView(safeContext).apply {
                text = "No expenses recorded yet."
                // Use a defined color; ensure R.color.gray exists or use android default
                try { setTextColor(ContextCompat.getColor(safeContext, R.color.gray)) }
                catch (e: Exception) { setTextColor(ContextCompat.getColor(safeContext, android.R.color.darker_gray)) }
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 32, 0, 32) // Add some padding
            }
            container.addView(noDataText)
            Log.d("ExpenseFragment", "updateExpenseProgressBars - Displayed 'No expenses' message.")
            return
        }

        val inflater = LayoutInflater.from(safeContext)
        // Determine default color safely
        val defaultColor = try { ContextCompat.getColor(safeContext, R.color.gray) }
        catch (e: Exception) { ContextCompat.getColor(safeContext, android.R.color.darker_gray) }

        // Loop through sorted expenses and create views
        for ((categoryName, amount) in expenses) {
            Log.d("ExpenseFragment", "Creating view for: $categoryName, Amount: $amount")
            val itemView = try {
                // *** INFLATE THE CORRECT LAYOUT: view_budget_progress_bar ***
                inflater.inflate(
                    R.layout.item_expense_progress, // Use the specified layout
                    container, // Pass the container view group
                    false
                )
            } catch (e: Exception) {
                Log.e("ExpenseFragment", "Failed to inflate layout R.layout.view_budget_progress_bar for $categoryName", e)
                continue // Skip this item if layout inflation fails
            }

            // Find views using IDs from view_budget_progress_bar.xml
            val nameTextView = itemView.findViewById<TextView>(R.id.categoryName)
            val percentageTextView = itemView.findViewById<TextView>(R.id.percentageText)
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
            val amountTextView = itemView.findViewById<TextView>(R.id.amountText)

            // Check if views were found (important!)
            if (nameTextView == null || percentageTextView == null || progressBar == null || amountTextView == null) {
                Log.e("ExpenseFragment", "Error finding views within R.layout.view_budget_progress_bar for $categoryName. Check layout file and IDs.")
                continue // Skip this item if views aren't found
            }

            // Populate the views
            nameTextView.text = categoryName
            amountTextView.text = currencyFormatter.format(amount)

            val percentage = if (totalExpenses > 0) (amount / totalExpenses * 100) else 0.0
            val percentageInt = percentage.toInt().coerceIn(0, 100)

            progressBar.max = 100
            progressBar.progress = percentageInt
            progressBar.visibility = View.VISIBLE // Ensure progress bar is visible

            // Apply color tint
            val color = colorMap[categoryName] ?: defaultColor
            progressBar.progressTintList = ColorStateList.valueOf(color)
            // Assumes R.drawable.custom_progress_bar is set in the XML layout

            // Set percentage text (e.g., "35%")
            try {
                percentageTextView.text = safeContext.getString(R.string.budget_percentage_format, percentageInt)
            } catch (e: Exception) {
                percentageTextView.text = "$percentageInt%"
                // Log.w("ExpenseFragment", "R.string.budget_percentage_format not found for $categoryName.")
            }

            container.addView(itemView) // Add the populated item view to the container
            Log.d("ExpenseFragment", "Added view for $categoryName with progress: $percentage%")
        }

        Log.d("ExpenseFragment", "Finished updateExpenseProgressBars.")
    }
}