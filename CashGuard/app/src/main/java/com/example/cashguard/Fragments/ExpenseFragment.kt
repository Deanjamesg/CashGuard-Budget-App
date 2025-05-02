package com.example.cashguard.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashguard.Activities.CategoryManagerActivity
import com.example.cashguard.Activities.TransactionsReportActivity

import com.example.cashguard.databinding.FragmentExpenseBinding

class ExpenseFragment : Fragment() {
    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!

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

        setupClickListeners()

        // Add this to setupClickListeners()
        binding.viewTransactionExpensePage.setOnClickListener {
            startActivity(Intent(requireContext(), TransactionsReportActivity::class.java))
        }
    }

    private fun setupClickListeners() {
        // Category Manager navigation
        binding.categoryManager.setOnClickListener {
            navigateToCategoryManager()
        }
    }

    private fun navigateToCategoryManager() {
        val intent = Intent(requireContext(), CategoryManagerActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}