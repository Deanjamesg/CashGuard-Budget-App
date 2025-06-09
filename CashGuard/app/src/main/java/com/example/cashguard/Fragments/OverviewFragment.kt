package com.example.cashguard.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashguard.ViewModel.OverviewFragmentViewModel
import com.example.cashguard.databinding.FragmentOverviewBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.cashguard.R
import org.eazegraph.lib.models.PieModel
import java.text.NumberFormat
import java.util.Locale

class OverviewFragment : Fragment() {

    // Using view binding to access UI elements safely
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!! // Non-null assertion shorthand

    // ViewModel dedicated to fetching and preparing data for this Overview screen
    private val viewModel: OverviewFragmentViewModel by viewModels()

    private val currencyFormatter =
        NumberFormat.getCurrencyInstance(Locale("en", "ZA")) // Adjust locale if needed

    var userIncome = 0.0
    var userExpense = 0.0

    // Standard fragment lifecycle method: create the view hierarchy
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Standard fragment lifecycle method: called after the view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("OverviewFragment", "On View Created")
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Log.d("OverviewFragment", "isLoading LiveData changed: $isLoading")
            if (isLoading) {
                Log.d("OverviewFragment", "TRUE")
                binding.loadingProgressBar.visibility = View.VISIBLE
            } else {
                Log.d("OverviewFragment", "FALSE")
                userIncome = viewModel.totalIncome.value ?: 0.0
                userExpense = viewModel.totalExpenses.value ?: 0.0
                setUpPieChart()
                binding.loadingProgressBar.visibility = View.GONE
            }
        })
        setUpClickListeners()

    }

    private fun setUpPieChart() {

        Log.d("OverviewFragment", "Set up Pie Chart")
        binding.pieChart.clearChart()

        val savings = userIncome - userExpense

        val expenseColor = ContextCompat.getColor(requireContext(), R.color.red)
        val savingsColor = ContextCompat.getColor(requireContext(), R.color.blue_light)
        val incomeColor = ContextCompat.getColor(requireContext(), R.color.green)
        val noDataColor = ContextCompat.getColor(requireContext(), R.color.dark_gray)

        var slicesAdded = false

        if (userExpense > 0) {
            binding.pieChart.addPieSlice(PieModel("Expenses", userExpense.toFloat(), expenseColor))
            binding.textViewExpenses.text = currencyFormatter.format(userExpense)
            slicesAdded = true
        }
        if (userIncome > 0) {
            binding.pieChart.addPieSlice(PieModel("Income", userIncome.toFloat(), incomeColor))
            binding.textViewIncome.text = currencyFormatter.format(userIncome)
            slicesAdded = true
        }
        if (savings > 0) {
            binding.pieChart.addPieSlice(PieModel("Savings", savings.toFloat(), savingsColor))
            binding.textViewSavings.text = currencyFormatter.format(savings)
            slicesAdded = true
        }
        if (userExpense == 0.0 && userIncome == 0.0) {
            binding.pieChart.addPieSlice(PieModel("No Data", 1f, noDataColor))
        } else if (userExpense <= 0 && userIncome > 0) {
            // This case means all income is effectively savings (or just income if no expenses concept)
            binding.pieChart.addPieSlice(
                PieModel(
                    "Income",
                    userIncome.toFloat(),
                    incomeColor
                )
            )
            Log.d("OverviewFragment", "Added pie slice for sole Income: ${userIncome.toFloat()}")
            slicesAdded = true
        }

        // Note: If expenses > income, savings will be negative, and that slice won't be added.
        // The chart will then primarily show expenses. The sum of slice values might not equal income in this case.
        // This is a common way to show it - how much was spent, and how much (if any) was saved.

        if (!slicesAdded) {
            binding.pieChart.addPieSlice(PieModel("No Data", 1f, noDataColor))
            Log.d("OverviewFragment", "Added 'No Data' pie slice.")
            binding.pieChart.isUseInnerValue = true
        }
        binding.pieChart.startAnimation()

    }

    private fun setUpClickListeners() {

        binding.buttonAddExpense.setOnClickListener {
            Log.d("Navigation", "Navigating to Add Transaction Fragment with type Expense")
            val bundle = Bundle().apply {
                putString("initialTransactionType", "Expense")
            }
            findNavController().navigate(R.id.action_addTransaction, bundle)
        }

        binding.buttonAddIncome.setOnClickListener {
            Log.d("Navigation", "Navigating to Add Transaction Fragment with type Income")
            val bundle = Bundle().apply {
                putString("initialTransactionType", "Income")
            }
            findNavController().navigate(R.id.action_addTransaction, bundle)
        }

        binding.buttonBudgetBalances.setOnClickListener {
            findNavController().navigate(R.id.action_budgetBalancesFragment)
        }

        binding.btnBudgetGraph.setOnClickListener {
            findNavController().navigate(R.id.action_barGraphFragment)
        }
    }

    override fun onDestroyView() {
        Log.d("OverviewFragment", "onDestroyed called")
        viewModel.refreshData()
        super.onDestroyView()
        _binding = null
    }


}