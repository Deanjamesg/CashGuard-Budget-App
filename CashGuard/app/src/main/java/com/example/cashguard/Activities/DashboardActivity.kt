package com.example.cashguard.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Activities.AddTransactionActivity
import com.example.cashguard.Adapter.TabsPagerAdapter
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.R
import com.example.cashguard.databinding.ActivityBudgetoverviewWithNavDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBudgetoverviewWithNavDrawerBinding
    lateinit var sharedViewModel: SharedViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetoverviewWithNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user ID from intent
      //val userId = intent.getIntExtra("USER_ID", -1)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Dashboard ID: ${sessionManager.getUserId()}")

        // Initialize SharedViewModel
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        sharedViewModel.userId = userId

        // Setup ViewPager and TabLayout
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        // Check if coming from home icon click
        if (intent.flags and Intent.FLAG_ACTIVITY_CLEAR_TOP != 0) {
            binding.viewPager.currentItem = 1 // Switch to Overview tab
        }

        // Setup ViewPager with Tabs
        viewPager.adapter = TabsPagerAdapter(this)

        // Setup TabLayout with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Budget"
                1 -> "Overview"
                else -> "Expenses"
            }
        }.attach()

        // Navigate to Overview tab (position 1)
        binding.homeIcon.setOnClickListener {
            binding.viewPager.currentItem = 1
        }

        // Navigate to Add Transaction Activity
        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", sharedViewModel.userId)
            }
            startActivity(intent)
        }

        // Navigate to Settings Activity
        binding.settingsIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Setup Navigation Drawer
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.navToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up the ActionBarDrawerToggle
        binding.navView.setNavigationItemSelectedListener(this)


    }

    // Handle navigation item clicks
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_transaction -> openAddTransaction()
            R.id.nav_create_budget -> openCreateBudget()
            R.id.nav_manage_categories -> openManageCategories()
            R.id.nav_signout -> signOut()
            // Add other cases for menu items
        }
        // Close the navigation drawer after item selection
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Open Add Transaction Activity
    private fun openAddTransaction() {
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivity(intent)
    }

    // Open Create Budget Activity
    private fun openCreateBudget() {
        // Implement create budget logic
    }

    private fun openManageCategories() {
        // Implement manage categories logic
    }

    private fun signOut() {
        finish()
    }

    // Handle back button press
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Handle navigation item selection
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
}