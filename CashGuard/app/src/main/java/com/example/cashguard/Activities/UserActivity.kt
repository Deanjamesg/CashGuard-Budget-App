package com.example.cashguard.Activities

import android.content.Intent
import android.os.Bundle
// import android.util.Log // No longer needed if removing specific logs
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
// import androidx.core.view.GravityCompat // Not directly used for open/close by ID
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.R
import com.example.cashguard.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)

        setSupportActionBar(binding.topAppBar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_user_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val topLevelDestinations = setOf(
            R.id.dashboardFragment,
            R.id.searchTransactionsFragment
        )
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)


        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.navViewDrawer.setupWithNavController(navController)

        binding.navViewDrawer.setNavigationItemSelectedListener { menuItem ->
            var itemHandled = false
            when (menuItem.itemId) {
                R.id.nav_signOut -> {
                    performSignOut()
                    itemHandled = true
                }
                else -> {
                    itemHandled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }

            binding.drawerLayout.closeDrawer(binding.navViewDrawer)
            itemHandled
        }



        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun performSignOut() {
        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show()

        sessionManager.signOut()
        val intent = Intent (this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toolbar_menu -> {
                if (binding.drawerLayout.isDrawerOpen(binding.navViewDrawer)) {
                    binding.drawerLayout.closeDrawer(binding.navViewDrawer)
                } else {
                    binding.drawerLayout.openDrawer(binding.navViewDrawer)
                }
                true
            }
            else -> NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.navViewDrawer)) {
            binding.drawerLayout.closeDrawer(binding.navViewDrawer)
        } else {
            super.onBackPressed()
        }
    }
}