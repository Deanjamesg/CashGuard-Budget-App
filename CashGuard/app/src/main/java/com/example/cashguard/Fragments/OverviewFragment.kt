package com.example.cashguard.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access views using binding
        //binding.monthText.text = "March"
        //binding.budgetText.text = "R15 000\nBudget"

        // Set up button click listeners, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}