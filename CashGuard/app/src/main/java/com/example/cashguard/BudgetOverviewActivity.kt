package com.example.cashguard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.example.cashguard.databinding.ActivityBudgetoverviewBinding

class BudgetOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetoverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetoverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Link ViewPager2 with TabLayout
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = TabsPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Budget"
                1 -> "Overview"
                2 -> "Expenses"
                else -> ""
            }
        }.attach()
    }
}