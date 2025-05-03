package com.example.cashguard.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.databinding.FragmentOverviewBinding
import android.content.Intent
import android.util.Log
import com.example.cashguard.Activities.BudgetBalancesActivity
import com.example.cashguard.databinding.ActivityOverviewBinding
import com.example.cashguard.ViewModel.SharedViewModel
import androidx.lifecycle.ViewModelProvider
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}