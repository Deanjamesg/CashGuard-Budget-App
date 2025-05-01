package com.example.cashguard.Acitivties

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Adapter.TabsPagerAdapter
import com.example.cashguard.ViewModel.SharedViewModel
import com.example.cashguard.R
import com.example.cashguard.databinding.ActivityBudgetoverviewWithNavDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBudgetoverviewWithNavDrawerBinding
    lateinit var sharedViewModel: SharedViewModel
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetoverviewWithNavDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get user ID from intent
        val userId = intent.getIntExtra("USER_ID", -1)

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


        viewPager.adapter = TabsPagerAdapter(this)

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

        binding.searchIcon.setOnClickListener {
            val intent = Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", sharedViewModel.userId)
            }
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

        binding.navView.setNavigationItemSelectedListener(this)


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