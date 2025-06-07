package com.example.cashguard.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cashguard.Fragments.BudgetFragment
import com.example.cashguard.Fragments.ExpenseFragment
import com.example.cashguard.Fragments.OverviewFragment

private const val NUM_TABS = 3

class DashboardPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BudgetFragment()
            1 -> OverviewFragment()
            2 -> ExpenseFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}