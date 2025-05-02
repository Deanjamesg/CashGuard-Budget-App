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
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Model.TransactionViewModel
import com.example.cashguard.Model.TransactionViewModelFactory
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.R
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.ViewModel.CategoryViewModel
import com.example.cashguard.databinding.ActivityBudgetoverviewWithNavDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBudgetoverviewWithNavDrawerBinding
    lateinit var sharedViewModel: SharedViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var sessionManager : SessionManager
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetoverviewWithNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        // ... (handle invalid userId if needed) ...
        Log.d("SESSION", "Dashboard ID: $userId")

        // --- Initialize ALL ViewModels ---
        Log.d("DashboardActivity", "Initializing ViewModels...")
        try {
            // SharedViewModel (This one is simple)
            sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
            sharedViewModel.userId = userId

            // --- Initialize TransactionViewModel using its Factory (REQUIRED) ---
            // 1. Get Database instance safely
            val database = AppDatabase.getInstance(applicationContext) // Use applicationContext for safety
            // 2. Create Repository instance
            val transactionRepository = TransactionRepository(database.transactionDao())
            // 3. Create the Factory
            val transactionFactory = TransactionViewModelFactory(transactionRepository)
            // 4. Initialize ViewModel using the Factory
            transactionViewModel = ViewModelProvider(this, transactionFactory)[TransactionViewModel::class.java] // Pass the factory!


            // Initialize CategoryViewModel using the default factory (This should be fine)
            categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

            Log.d("DashboardActivity", "ViewModels Initialized Successfully.")

        } catch (e: Exception) {
            Log.e("DashboardActivity", "CRITICAL ERROR initializing ViewModels!", e)
            // Show error to user / Close activity if ViewModels are essential
            // Toast.makeText(this, "Failed to load essential components.", Toast.LENGTH_LONG).show()
            finish() // Example: Close activity if initialization fails
            return
        }

        // --- Rest of your onCreate setup ---
        setupNavigationDrawer() // Example call
        setupViewPagerAndTabs() // Example call
        setupIconListeners()    // Example call

        Log.d("DashboardActivity", "onCreate completed.")
    }

    // --- Helper methods for setup --- (Break down your onCreate logic)

    private fun setupViewPagerAndTabs() {
        Log.d("DashboardActivity", "Setting up ViewPager and Tabs...")
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = TabsPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.tab_budget)
                1 -> getString(R.string.tab_overview)
                2 -> getString(R.string.tab_expenses)
                else -> null
            }
        }.attach()
        // Set default tab if needed
        // binding.viewPager.currentItem = 1 // Example: Overview default
        Log.d("DashboardActivity", "ViewPager and Tabs setup complete.")
    }

    private fun setupIconListeners() {
        Log.d("DashboardActivity", "Setting up Icon Listeners...")
        binding.homeIcon.setOnClickListener { binding.viewPager.currentItem = 1 }
        binding.searchIcon.setOnClickListener { /* ... start SearchByDateActivity ... */ }
        binding.settingsIcon.setOnClickListener { /* ... start SettingsActivity ... */ }
        Log.d("DashboardActivity", "Icon Listeners setup complete.")
    }

    private fun setupNavigationDrawer() {
        Log.d("DashboardActivity", "Setting up Navigation Drawer...")
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.navToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)
        // setSupportActionBar(binding.navToolbar) // If using as ActionBar
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("DashboardActivity", "Navigation Drawer setup complete.")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_transaction -> openAddTransaction()
            R.id.nav_create_budget -> openCreateBudget()
            R.id.nav_manage_categories -> openManageCategories()
            R.id.nav_signout -> signOut()
            // Add other cases for menu items
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openAddTransaction() {
        val intent = Intent(this, AddTransactionActivity::class.java)
        startActivity(intent)
    }

    private fun openCreateBudget() {
        // Implement create budget logic
    }

    private fun openManageCategories() {
        // Implement manage categories logic
    }

    private fun signOut() {
        finish()
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
}