package com.example.cashguard.Acitivties

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Adapter.TabsPagerAdapter
import com.example.cashguard.Model.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.example.cashguard.databinding.ActivityBudgetoverviewBinding

class BudgetOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetoverviewBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetoverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user ID from intent
        val userId = intent.getIntExtra("USER_ID", -1)

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.userId = userId

        // Setup ViewPager and TabLayout
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = TabsPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Budget"
                1 -> "Overview"
                else -> "Expenses"
            }
        }.attach()
    }
}