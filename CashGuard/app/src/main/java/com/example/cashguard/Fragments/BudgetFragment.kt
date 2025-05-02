package com.example.cashguard.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Activities.BudgetManagerActivity
import com.example.cashguard.Activities.DashboardActivity
import com.example.cashguard.Activities.AddTransactionActivity
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.R
import com.example.cashguard.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get SharedViewModel from activity
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.btnAddExpense.setOnClickListener {
            launchAddTransaction("Expense")
            Log.d("Button", "Expense")
        }

        binding.btnAddIncome.setOnClickListener {
            launchAddTransaction("Income")
            Log.d("Button", "Income")
        }

        binding.btnBudgetManager.setOnClickListener {
            launchBudgetManager()
            Log.d("Button", "Budget Manager")
        }

    }

    private fun launchBudgetManager() {

        Log.d("Function", "LAUNCH")
        try {
            val userId = sharedViewModel.userId.takeIf { it != -1 } ?: run {
                Toast.makeText(requireContext(), "User session expired", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                return
            }

            val intent = Intent(requireActivity(), BudgetManagerActivity::class.java).apply {
                putExtra("USER_ID", userId)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                Log.d("Button", "INTENT")
            }

            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("BudgetFragment", "Navigation error", e)
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
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("BudgetFragment", "Navigation error", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private fun launchAddTransaction(budgetFragment: BudgetFragment, transactionType: String) {
            val userId = (budgetFragment.activity as DashboardActivity).sharedViewModel.userId
            if (userId == -1) {
                Toast.makeText(budgetFragment.requireContext(), "Session expired", Toast.LENGTH_SHORT).show()
                budgetFragment.activity?.finish()
                return
            }

            val intent = Intent(budgetFragment.requireActivity(), AddTransactionActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }
            budgetFragment.startActivity(intent)
        }
    }
}

