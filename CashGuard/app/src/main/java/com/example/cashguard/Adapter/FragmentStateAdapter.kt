package com.example.cashguard.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cashguard.Fragments.BudgetFragment
import com.example.cashguard.Fragments.ExpenseFragment
import com.example.cashguard.Fragments.OverviewFragment

class TabsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BudgetFragment()    // First tab: Budget
            1 -> OverviewFragment()  // Second tab: Overview
            2 -> ExpenseFragment()  // Third tab: Expenses
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}