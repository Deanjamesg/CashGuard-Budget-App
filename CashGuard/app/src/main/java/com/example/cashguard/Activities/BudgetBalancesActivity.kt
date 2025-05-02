package com.example.cashguard.Acitivties

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf // For creating fragment arguments easily
import com.example.cashguard.Adapter.BudgetCategoryAdapter
import com.example.cashguard.Fragments.BudgetBalancesFragment
import com.example.cashguard.R
import com.example.cashguard.databinding.ActivityBudgetBalancesBinding
import com.example.cashguard.ViewModel.BudgetInfo

class BudgetBalancesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBalancesBinding
    private var userId: Int = -1
    //private lateinit var progressAdpter: BudgetCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBalancesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the userId from the Intent
        userId = intent.getIntExtra("USER_ID", -1)

        if (userId == -1) {
            finish() // Close the activity if no user ID
            return
        }

        // --- Setup Toolbar ---
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        // --- End Toolbar Setup ---


        // Load the fragment only if it's the first creation
        if (savedInstanceState == null) {
            val fragment = BudgetBalancesFragment().apply {
                // Pass the userId to the fragment using arguments Bundle
                arguments = bundleOf("USER_ID" to userId)
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.budget_balances_fragment_container, fragment)
                .commit()
        }
    }

    // Handle Toolbar back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // More modern way to handle back press
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}