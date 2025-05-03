package com.example.cashguard.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.databinding.FragmentOverviewBinding
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.cashguard.Acitivties.BudgetBalancesActivity
import com.example.cashguard.databinding.ActivityOverviewBinding
import com.example.cashguard.ViewModel.SharedViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Activities.TransactionsReportActivity
import com.example.cashguard.R
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

        // Set up button click listeners, etc.
        binding.buttonBudgetBalances.setOnClickListener {
            val intent = Intent(requireActivity(), BudgetBalancesActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.buttonBudgetBalances.setOnClickListener {
            val intent = Intent(requireActivity(), BudgetBalancesActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnViewTransactions2.setOnClickListener {
            launchTransactionReport()
            Log.d("Button", "View Transactions")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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