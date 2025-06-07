package com.example.cashguard.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.Adapter.BudgetBalanceAdapter
import com.example.cashguard.ViewModel.BudgetBalancesViewModel
import com.example.cashguard.databinding.FragmentBudgetBalancesBinding

class BudgetBalancesFragment : Fragment() {

    private var _binding: FragmentBudgetBalancesBinding? = null
    private val binding get() = _binding!!

    private val progressBarAdapter = BudgetBalanceAdapter()

    private val viewModel: BudgetBalancesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBalancesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.budgetRecyclerView.adapter = progressBarAdapter

        setupFab()
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.progressBarData.observe(viewLifecycleOwner) { progressBarList ->
            progressBarAdapter.submitList(progressBarList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // if (isLoading) { /* show progress spinner */ } else { /* hide spinner */ }
        }
    }

    private fun setupFab() {
        binding.fabAddBudget.setOnClickListener {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}