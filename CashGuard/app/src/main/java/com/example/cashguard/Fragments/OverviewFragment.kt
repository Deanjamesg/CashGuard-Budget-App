package com.example.cashguard.Fragments

// Android & System Imports
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log // Keep Log import
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.text.NumberFormat // Still needed for center text formatting
import java.util.Locale // Still needed for center text formatting

// MPAndroidChart Imports
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF

// Project Specific Imports
import com.example.cashguard.Acitivties.BudgetBalancesActivity
import com.example.cashguard.Activities.AddTransactionActivity
import com.example.cashguard.R
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.ViewModel.OverviewData
import com.example.cashguard.ViewModel.OverviewViewModel
import com.example.cashguard.ViewModel.OverviewViewModelFactory
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.databinding.FragmentOverviewBinding

import com.example.cashguard.databinding.FragmentOverviewBinding
import android.content.Intent
import android.util.Log
import com.example.cashguard.Activities.BudgetBalancesActivity
import android.widget.Toast
import com.example.cashguard.databinding.ActivityOverviewBinding
import com.example.cashguard.ViewModel.SharedViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Activities.TransactionsReportActivity
import com.example.cashguard.R
import com.example.cashguard.ViewModel.BudgetInfo


class OverviewFragment : Fragment() {

    // Log tag for filtering output
    private val TAG = "OverviewLogic"

    // Using view binding to access UI elements safely
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!! // Non-null assertion shorthand

    // Shared ViewModel, potentially used for inter-fragment communication
    private lateinit var sharedViewModel: SharedViewModel
    // ViewModel dedicated to fetching and preparing data for this Overview screen
    private lateinit var overviewViewModel: OverviewViewModel

    // Standard fragment lifecycle method: create the view hierarchy
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: Inflating fragment layout (fragment_overview.xml)")
        // Inflate the layout using view binding
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        // Return the root view of the inflated layout
        return binding.root
    }

    // Standard fragment lifecycle method: called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: Fragment view is ready.")

        // Initialize the SharedViewModel, typically scoped to the host Activity
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        Log.d(TAG, "onViewCreated: SharedViewModel initialized.")

        // Set up our specific ViewModel for the overview data and start listening
        initializeOverviewViewModel()

        // Assign actions to the buttons in our layout
        setupButtonClickListeners()


    }

    // Sets up the OverviewViewModel, handling dependencies and user login state
    private fun initializeOverviewViewModel() {
        Log.d(TAG, "initializeOverviewViewModel: Preparing to create OverviewViewModel.")

        // Need the current user's ID from SessionManager to fetch their data
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        Log.i(TAG, "initializeOverviewViewModel: Retrieved userId = $userId")

        // Crucial check: Only proceed if we have a valid user ID
        if (userId != -1) {
            // Manually provide dependencies for the ViewModel's factory
            // This avoids needing a full dependency injection framework for this example
            Log.d(TAG, "initializeOverviewViewModel: Valid userId found. Setting up repository and factory.")
            val transactionDao = AppDatabase.getInstance(requireContext()).transactionDao()
            val transactionRepository = TransactionRepository(transactionDao)
            val factory = OverviewViewModelFactory(transactionRepository, userId)

            // Create or retrieve the ViewModel instance associated with this fragment
            overviewViewModel = ViewModelProvider(this, factory).get(OverviewViewModel::class.java)
            Log.i(TAG, "initializeOverviewViewModel: OverviewViewModel instance obtained.")

            // Start observing the LiveData from the ViewModel for data updates
            observeOverviewData()
        } else {
            // Handle the case where the user isn't logged in (no valid ID)
            Log.e(TAG, "initializeOverviewViewModel: Invalid userId ($userId). Cannot load overview. Displaying error state.")
            // Update the UI to reflect this error state
            binding.circularChart.centerText = "Error:\nLogin Required"
            binding.circularChart.invalidate()
        }
    }

    // Sets up the LiveData observer to react to data changes from the OverviewViewModel
    private fun observeOverviewData() {
        Log.d(TAG, "observeOverviewData: Setting up observer on overviewViewModel.overviewData")
        // The observer lambda is called automatically when the LiveData value changes
        overviewViewModel.overviewData.observe(viewLifecycleOwner, Observer { overviewData ->
            // This is where we get the calculated Income, Expenses, and Savings
            Log.i(TAG, "observeOverviewData: LiveData updated! Data: $overviewData")

            // Decide whether to draw the chart or show a "no data" message
            val shouldSetupChart = overviewData.totalIncome > 0 || overviewData.totalExpenses > 0
            Log.d(TAG, "observeOverviewData: Checking if chart should be drawn (Income or Expenses > 0): $shouldSetupChart")

            if (shouldSetupChart) {
                // We have data, call the function to configure the chart visually
                setupPieChart(binding.circularChart, overviewData)
            } else {
                // No significant data for the period, clear the chart
                Log.d(TAG, "observeOverviewData: No data to display on chart. Clearing it.")
                binding.circularChart.clear()
                binding.circularChart.centerText = "No data\nfor this month"
                binding.circularChart.invalidate() // Redraw the chart (now empty)
            }

        })
    }

    // Configures and populates the PieChart view with the received overview data
    private fun setupPieChart(pieChart: PieChart, data: OverviewData) {
        Log.i(TAG, "setupPieChart: Configuring PieChart view with data: $data")

        // Basic Chart Appearance
        pieChart.isDrawHoleEnabled = true         // Make it a donut
        pieChart.setUsePercentValues(true)        // Display slice values as percentages
        pieChart.setEntryLabelTextSize(0f)       // Hide labels like "Expense", "Savings" on the slices
        pieChart.description.isEnabled = false    // Remove the default description text
        pieChart.legend.isEnabled = false         // Remove the color legend box
        pieChart.isDrawRoundedSlicesEnabled

        //  Donut Hole Customization
        pieChart.holeRadius = 65f                 // Make the hole quite large (percentage of radius)
        pieChart.transparentCircleRadius = 68f    // Add a slightly larger transparent edge around the hole
        pieChart.setHoleColor(Color.TRANSPARENT)  // Make the center hole transparent

        //  Center Text (Savings Amount and Percentage)
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.WHITE)  // Ensure text is visible on background
        val formatCurrency = NumberFormat.getCurrencyInstance(Locale("en", "ZA")) // For R currency
        // Calculate savings as a percentage of total income (handle division by zero)
        val savingsPercent = if (data.totalIncome > 0) (data.savings / data.totalIncome) * 100 else 0.0
        // Create the multi-line text string for the center
        val centerText = "${formatCurrency.format(data.savings)}\nSavings (${String.format("%.0f", savingsPercent)}%)"
        pieChart.centerText = centerText
        Log.d(TAG, "setupPieChart: Set center text to '$centerText'")

        // Chart Interaction
        pieChart.rotationAngle = -90f             // Start drawing slices from the top (12 o'clock position)
        pieChart.isRotationEnabled = false        // Disable manual rotation
        pieChart.isHighlightPerTapEnabled = true  // Allow tapping slices to highlight them

        // Data Preparation (Creating Slices)
        Log.d(TAG, "setupPieChart: Preparing data entries (slices).")
        val entries = ArrayList<PieEntry>()
        // Only add slices for non-zero values to avoid tiny/invisible slices
        if (data.totalExpenses > 0) {
            entries.add(PieEntry(data.totalExpenses.toFloat(), "Expenses")) // Value determines slice size
            Log.d(TAG, "setupPieChart: Added Expense entry: ${data.totalExpenses.toFloat()}")
        }
        if (data.savings > 0) {
            entries.add(PieEntry(data.savings.toFloat(), "Savings"))
            Log.d(TAG, "setupPieChart: Added Savings entry: ${data.savings.toFloat()}")
        } else if (data.totalExpenses <= 0 && data.totalIncome > 0) {
            // If only income exists, represent it as a full circle using the 'savings' color
            entries.add(PieEntry(data.totalIncome.toFloat(), "Income Only"))
            Log.d(TAG, "setupPieChart: Added Income Only entry: ${data.totalIncome.toFloat()}")
        } else if (entries.isEmpty()) {
            // If list is still empty (no income/expense/savings), add a dummy slice
            entries.add(PieEntry(1f, "")) // Small value, empty label for visual placeholder
            Log.d(TAG, "setupPieChart: Added dummy empty state entry.")
        }

        // DataSet Configuration (Holds the entries and their styling)
        val dataSet = PieDataSet(entries, "") // Dataset label (not shown)
        dataSet.sliceSpace = 3f // Add a visual gap between slices

        //  Setting Colors Dynamically
        Log.d(TAG, "setupPieChart: Determining slice colors based on data.")
        val colors = ArrayList<Int>()
        try {
            // Use ContextCompat for safe color resource loading
            if (data.totalExpenses > 0 && data.savings > 0) {
                // Normal case: expense and savings exist
                colors.add(ContextCompat.getColor(requireContext(), R.color.pie_chart_expense))
                colors.add(ContextCompat.getColor(requireContext(), R.color.pie_chart_savings))
                Log.d(TAG, "setupPieChart: Using Expense and Savings colors.")
            } else if (data.totalExpenses <= 0 && data.totalIncome > 0) {
                // Only income case
                colors.add(ContextCompat.getColor(requireContext(), R.color.pie_chart_savings))
                Log.d(TAG, "setupPieChart: Using Savings color for Income-only.")
            } else {
                // Empty/Dummy slice case
                colors.add(Color.GRAY)
                Log.d(TAG, "setupPieChart: Using Gray color for empty state.")
            }
        } catch (e: Exception) {
            // Fallback if color resources are somehow missing
            Log.e("OverviewFragment", "Error loading pie chart colors! Check R.color values.", e)
            colors.add(Color.RED); colors.add(Color.GREEN); colors.add(Color.GRAY)
        }
        dataSet.colors = colors // Apply the chosen colors to the dataset

        //  Configuring Value Display (Percentages outside slices)
        dataSet.setDrawValues(true) // Tell the chart to draw values for slices
        dataSet.valueTextColor = Color.WHITE // Set color for the percentage text
        dataSet.valueTextSize = 6f          // Set text size for percentages
        // Position the values outside the pie slices
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // Configure the connector lines from slices to values
        dataSet.valueLinePart1OffsetPercentage = 80f // Start line near edge of slice
        dataSet.valueLinePart1Length = 0.5f          // Length of the first (angled) part of line
        dataSet.valueLinePart2Length = 0.2f          // Length of the second (horizontal) part of line
        dataSet.valueLineWidth = 1f                  // Line thickness

        // Use a formatter to show percentages correctly
        val percentFormatter = PercentFormatter(pieChart)

        // Final Assembly & Display
        Log.d(TAG, "setupPieChart: Creating PieData object and applying to chart.")
        // Create the PieData object containing our styled DataSet
        val pieData = PieData(dataSet)
        // Apply the percentage formatter to the data values
        pieData.setValueFormatter(percentFormatter)
        // Ensure the text size and color settings are applied to the PieData object as well
        pieData.setValueTextSize(dataSet.valueTextSize)
        pieData.setValueTextColor(dataSet.valueTextColor)

        // Set the final data on the PieChart view
        pieChart.data = pieData
        // Animate the chart drawing process for a nice visual effect
        pieChart.animateY(1000) // Animate over 1 second
        // Tell the chart to refresh and redraw itself with the new data and settings
        pieChart.invalidate()
        Log.i(TAG, "setupPieChart: Chart setup complete.")
    }

    // Assigns actions (navigation, etc.) to the buttons
    private fun setupButtonClickListeners() {
        Log.d(TAG, "setupButtonClickListeners: Setting onClick listeners for buttons.")
        // Navigate to Budget Balances screen
        binding.buttonBudgetBalances.setOnClickListener {
            Log.d(TAG, "buttonBudgetBalances clicked.")
            val intent = Intent(requireActivity(), BudgetBalancesActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddExpense.setOnClickListener {
            launchAddTransaction("Expense")
            Log.d("Button", "Expense")
        }

        binding.btnAddIncome.setOnClickListener {
            launchAddTransaction("Income")
            Log.d("Button", "Income")
        }
    }

    private fun launchAddTransaction(transactionType: String) {
        try {
            val userId = sharedViewModel.userId.takeIf { it != -1 } ?: run {
                Toast.makeText(requireContext(), "User session expired", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                return
            }

            val intent = Intent(requireActivity(), AddTransactionActivity::class.java).apply {
                putExtra("TRANSACTION_TYPE", transactionType)
                putExtra("USER_ID", userId)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            startActivity(intent)
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("BudgetFragment", "Navigation error", e)
        }

        binding.btnViewTransactions2.setOnClickListener {
            launchTransactionReport()
            Log.d("Button", "View Transactions")
        }
    }

    // Standard fragment lifecycle method: clean up when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: View destroyed, nullifying binding.")
        // Setting binding to null helps prevent memory leaks with view binding in fragments
        _binding = null
    }

    private fun launchTransactionReport() {
        try {
            val userId = sharedViewModel.userId.takeIf { it != -1 } ?: run {
                Toast.makeText(requireContext(), "User session expired", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                return
            }

            val intent = Intent(requireActivity(), TransactionsReportActivity::class.java).apply {
                putExtra("USER_ID", userId)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("BudgetFragment", "Navigation error", e)
        }
    }
}