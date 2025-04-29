package com.example.cashguard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Now you can access views using binding
        // Example:
        //binding.budgetCircle.text = "R15 000\nBudget\n\n- R5000\nExpenses\n\nR10 000\nBalance"

        // Set click listeners for buttons, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}