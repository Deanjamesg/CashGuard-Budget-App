package com.example.cashguard.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cashguard.R
import com.example.cashguard.ViewModel.BudgetFragmentViewModel
import com.example.cashguard.databinding.FragmentBudgetBinding
import org.eazegraph.lib.models.PieModel
import java.text.NumberFormat
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetFragmentViewModel by viewModels()

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.pieChart.visibility = View.GONE
            } else {
                binding.loadingProgressBar.visibility = View.GONE
                binding.pieChart.visibility = View.VISIBLE
                updateUIAndPieChart()
            }
        }

        setUpClickListeners()

        viewModel.expenseData.observe(viewLifecycleOwner) {
            if (!isAdded) return@observe
            updateUIAndPieChart()
        }

        viewModel.budgetData.observe(viewLifecycleOwner) {
            if (!isAdded) return@observe
            updateUIAndPieChart()
        }
    }

    private fun setUpClickListeners() {
        binding.buttonBudget.setOnClickListener {
            findNavController().navigate(R.id.action_budgetManagerFragment)
        }

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
    }

    private fun updateUIAndPieChart() {

        val budgetTotal = viewModel.budgetData.value ?: 0.0

        if (budgetTotal > 0) {
            val currentExpenses = viewModel.expenseData.value ?: emptyList()

            val totalSpent = currentExpenses.sumOf { it.totalAmount }

            binding.textViewBudget.text = currencyFormatter.format(budgetTotal)
            binding.textViewExpenses.text = currencyFormatter.format(totalSpent)
            binding.textViewBalance.text = currencyFormatter.format(budgetTotal - totalSpent)

            binding.pieChart.clearChart()
            val darkGrayColor = ContextCompat.getColor(requireContext(), R.color.dark_gray)

            // Expenses exist. Add each expense as a slice.
            currentExpenses.forEach { expense ->
                binding.pieChart.addPieSlice(
                    PieModel(
                        expense.categoryName,
                        expense.totalAmount.toFloat(),
                        expense.categoryColor
                    )
                )
            }
            // Add the "Remaining" part of the budget as a dark gray slice, if any.
            val remainingBudget = budgetTotal - totalSpent
            if (remainingBudget > 0.001) { // Use a small epsilon for float comparison
                binding.pieChart.addPieSlice(
                    PieModel(
                        "Remaining Budget",
                        remainingBudget.toFloat(),
                        darkGrayColor
                    )
                )
            }
            // If totalSpent >= budgetTotal, no "Remaining Budget" slice is added,
            // or it would be zero/negative. The pie will consist of expense slices.
            // The text views will indicate if overspent.
            binding.pieChart.setUseInnerValue(false) // Ensure inner value is off if slices are present
        } else {
            val darkGrayColor = ContextCompat.getColor(requireContext(), R.color.dark_gray)
            binding.pieChart.addPieSlice(PieModel("Create a Budget", 1f, darkGrayColor))
            binding.pieChart.setUseInnerValue(true)
            binding.pieChart.invalidate() // Refresh to show inner text
        }

        binding.pieChart.startAnimation()

    }

    override fun onResume() {
        super.onResume()
        Log.d("BudgetFragment", "onResume called")
    }

    override fun onDestroyView() {
        viewModel.refreshData()
        super.onDestroyView()
        _binding = null
    }
}