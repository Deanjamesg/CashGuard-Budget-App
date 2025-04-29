package com.example.cashguard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2  // Number of tabs/fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BudgetFragment()
            1 -> OverviewFragment()
            else -> BudgetFragment() // Default to Budget if needed
        }
    }
}