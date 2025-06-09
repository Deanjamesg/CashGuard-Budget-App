package com.example.cashguard.Fragments

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.cashguard.R
import com.example.cashguard.ViewModel.ExpenseFragmentViewModel
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.databinding.FragmentExpenseBinding
import org.eazegraph.lib.models.PieModel
import java.text.NumberFormat
import java.util.Locale

class ExpenseFragment : Fragment() {
    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseFragmentViewModel by viewModels()

    // UI References and User Info
    private lateinit var categoryListContainer: LinearLayout

    // Utilities
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Fragment", "Expense Fragment Created.")

        categoryListContainer = binding.expenseCategoryListContainer

        setupClickListeners()

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Log.d("ExpenseFragment", "isLoading LiveData changed: $isLoading")
            if (isLoading) {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.scrollViewContent.visibility = View.GONE // Hide content while loading
            } else {
                binding.loadingProgressBar.visibility = View.GONE
                binding.scrollViewContent.visibility = View.VISIBLE // Show content when loaded
            }
        })

        viewModel.expenseBarData.observe(viewLifecycleOwner, Observer { expenseBars ->
            Log.d("ExpenseFragment", "expenseBarData LiveData changed. Item count: ${expenseBars?.size ?: "null"}")
            // The ViewModel should ideally always provide a non-null list (even if empty)
            setUpExpenseBar(expenseBars ?: emptyList())
        })
    }

    private fun setUpExpenseBar(expenseBarList: List<ExpenseBar>) {

        Log.d("ExpenseFragment", "Setup Expense Bar Called")

        binding.pieChart.clearChart()

        if (expenseBarList.isEmpty()) {

            val noDataTextColor = ContextCompat.getColor(requireContext(), R.color.light_gray)
            val noDataChartColor = ContextCompat.getColor(requireContext(), R.color.dark_gray)
            binding.pieChart.addPieSlice(PieModel("No Data", 1f, noDataChartColor))
            binding.pieChart.isUseInnerValue = true

            binding.textViewCategoryTotalExpensesTitle.visibility = View.GONE

            binding.totalExpensesText.setTextColor(noDataTextColor)
            binding.textViewTotalExpensesLabel.setTextColor(noDataTextColor)

            Log.d("ExpenseFragment", "EMPTY LIST")

            return
        }
        // Setting up the Total Expenses TextView
        var expenseTotal = 0.0
        for (expenseBar in expenseBarList) {
            expenseTotal += expenseBar.totalAmount
        }
        binding.totalExpensesText.text = currencyFormatter.format(expenseTotal)

        val container = binding.expenseCategoryListContainer
        container.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        for (expenseBar in expenseBarList) {
            val item = inflater.inflate(R.layout.item_expense_progress, container, false)

            // Find the views within the inflated item_category.xml
            val categoryNameTV = item.findViewById<TextView>(R.id.categoryName)
            val percentageTV = item.findViewById<TextView>(R.id.percentageText)
            val progressBar = item.findViewById<ProgressBar>(R.id.progressBar)
            val amountTV = item.findViewById<TextView>(R.id.amountText)

            categoryNameTV.text = expenseBar.categoryName

            val percentage = if (expenseTotal > 0) (expenseBar.totalAmount / expenseTotal * 100) else 0.0
            val percentageInt = percentage.toInt().coerceIn(0, 100)

            progressBar.max = 100
            progressBar.progress = percentageInt
            progressBar.visibility = View.VISIBLE
            percentageTV.text = getString(R.string.budget_percentage_format, percentageInt)
            amountTV.text = currencyFormatter.format(expenseBar.totalAmount)

            var color = ContextCompat.getColor(requireContext(), R.color.gray)

            if (expenseBar.categoryColor != null) {
                color = expenseBar.categoryColor
            }
            // Apply color tint
            progressBar.progressTintList = ColorStateList.valueOf(color)
            // Add the item view to the container
            container.addView(item)
            Log.d("ExpenseFragment", "Added view for ${expenseBar.categoryName} with progress: $percentageInt%")


            // PIE CHART

            binding.pieChart.addPieSlice(PieModel(
                expenseBar.categoryName,
                expenseBar.totalAmount.toFloat(),
                expenseBar.categoryColor
            ))
        }

//        binding.pieChart.startAnimation()

    }

    override fun onResume() {
        viewModel.refreshExpenseBars()
        super.onResume()
    }

    override fun onDestroyView() {
        viewModel.refreshExpenseBars()
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {

        binding.buttonAddExpense.setOnClickListener {
            val bundle = Bundle().apply {
                putString("initialTransactionType", "Expense")
            }
            findNavController().navigate(R.id.action_addTransaction, bundle)
        }
        binding.buttonAddIncome.setOnClickListener {
            val bundle = Bundle().apply {
                putString("initialTransactionType", "Income")
            }
            findNavController().navigate(R.id.action_addTransaction, bundle)
        }

        binding.categoryManager.setOnClickListener {
            findNavController().navigate(R.id.action_categoriesFragment)
        }

        binding.searchTransactions.setOnClickListener {
            findNavController().navigate(R.id.action_searchTransactionsFragment)
        }
    }
}