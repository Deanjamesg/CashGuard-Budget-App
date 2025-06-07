package com.example.cashguard.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cashguard.Adapter.DashboardPagerAdapter
import com.example.cashguard.R
import com.example.cashguard.databinding.FragmentDashboardBinding
import com.google.android.material.tabs.TabLayoutMediator

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var justCreated = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = DashboardPagerAdapter(this)
        binding.viewPagerDashboard.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerDashboard) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_budget)
                1 -> getString(R.string.tab_overview)
                2 -> getString(R.string.tab_expenses)
                else -> null
            }
        }.attach()
        if (justCreated) {
            binding.viewPagerDashboard.currentItem = 1
            justCreated = false
        }
    }

    override fun onResume() {
        Log.d("DashboardFragment", "onResume called")
        super.onResume()
    }

    override fun onDestroyView() {
        Log.d("DashboardFragment", "onDestroyed called")
        super.onDestroyView()
        _binding = null
    }
}