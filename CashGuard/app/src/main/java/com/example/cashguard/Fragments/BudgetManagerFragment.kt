package com.example.cashguard.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.Adapter.BudgetManagerAdapter
import com.example.cashguard.ViewModel.BudgetManagerViewModel
import com.example.cashguard.databinding.FragmentBudgetManagerBinding
import java.util.Locale

class BudgetManagerFragment : Fragment() {

    private var _binding: FragmentBudgetManagerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetManagerViewModel by viewModels()
    private lateinit var budgetAdapter: BudgetManagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        budgetAdapter = BudgetManagerAdapter()

        // REPAIRED: The listener is no longer set on the adapter.
        // budgetAdapter.setOnBudgetChangedListener(this)

        setupRecyclerView()
        setUpObservers()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.submitButton.setOnClickListener {
            val changedCategories = budgetAdapter.getChangedCategories()

            if (changedCategories.isNotEmpty()) {
                val totalBudget = budgetAdapter.calculateNewTotal()

                viewModel.saveBudgetOnClick(changedCategories, totalBudget)

                Toast.makeText(requireContext(), "Budget updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No changes to save.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategories.apply {
            adapter = budgetAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            budgetAdapter.submitList(categories)
        }

        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            budget?.let {
                val formattedTotal = String.format(Locale.getDefault(), "%.0f", it.budgetAmount ?: 0.0)
                binding.tvBudgetTotal.text = formattedTotal
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}