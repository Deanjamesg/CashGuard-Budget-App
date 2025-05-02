package com.example.cashguard.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.databinding.FragmentOverviewBinding
import android.content.Intent
import android.util.Log
import com.example.cashguard.Acitivties.BudgetBalancesActivity
import com.example.cashguard.databinding.ActivityOverviewBinding
import com.example.cashguard.ViewModel.SharedViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Activities.TransactionsReportActivity
import com.example.cashguard.ViewModel.BudgetInfo

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        // Access views using binding
        //binding.monthText.text = "March"
        //binding.budgetText.text = "R15 000\nBudget"

        // Set up button click listeners, etc.
        binding.buttonBudgetBalances.setOnClickListener {
            val userId = sharedViewModel.userId // Get userId from the initialized ViewModel
            if (userId != null && userId != -1) {
                // Create Intent for the new Activity
                val intent = Intent(requireActivity(), BudgetBalancesActivity::class.java).apply {
                    // Pass the userId to the new Activity
                    putExtra("USER_ID", userId)
                }
                startActivity(intent) // Launch the Activity
            } else {
                Log.e("OverviewFragment", "Cannot navigate to Budget Balances: User ID is invalid ($userId)")
                // Optional: Show a Toast or error message to the user
            }
        }

        // Add this to onViewCreated()
        binding.viewTransactionOverviewPage.setOnClickListener {
            startActivity(Intent(requireContext(), TransactionsReportActivity::class.java))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}