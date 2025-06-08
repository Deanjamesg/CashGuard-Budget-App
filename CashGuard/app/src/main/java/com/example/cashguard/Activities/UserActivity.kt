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

        // Initialize SessionManager
        sessionManager = SessionManager(applicationContext)

        setSupportActionBar(binding.topAppBar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_user_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations for AppBarConfiguration
        // These destinations will not show an "up" arrow in the AppBar.
        // The drawer icon will be shown instead.
        val topLevelDestinations = setOf(
            R.id.dashboardFragment,
            R.id.searchTransactionsFragment
            // Add other fragment IDs that are top-level destinations accessible from the drawer/bottomNav
            // For example, if addTransactionFragment and categoryManagerFragment are top-level:
            // R.id.addTransactionFragment,
            // R.id.categoryManagerFragment
        )
        // Pass the drawerLayout to AppBarConfiguration if you want the hamburger icon
        // to automatically open the drawer for top-level destinations.
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)


        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))

        supportActionBar?.setDisplayShowTitleEnabled(false) // Keep custom title handling if any

        // Setup BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Setup NavigationView (Drawer) with NavController
        // This handles navigation for most items automatically based on their IDs matching destination IDs.
        // We'll override for specific items like nav_signOut.
        binding.navViewDrawer.setupWithNavController(navController) // Recommended for standard navigation

        // Custom handling for specific drawer items like Sign Out
        binding.navViewDrawer.setNavigationItemSelectedListener { menuItem ->
            var itemHandled = false
            when (menuItem.itemId) {
                R.id.nav_signOut -> {
                    performSignOut()
                    itemHandled = true
                }
                // Add other custom handlers here if NavigationUI.onNavDestinationSelected is not sufficient
                // For example:
                // R.id.nav_export -> {
                //     Toast.makeText(this, "Export to Excel Clicked (Custom Action)", Toast.LENGTH_SHORT).show()
                //     // Perform custom export logic
                //     itemHandled = true
                // }
                else -> {
                    // Let NavigationUI handle standard navigation for other items
                    // This will navigate to destinations whose ID matches the menu item's ID.
                    itemHandled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                }
            }

            // Close the drawer
            binding.drawerLayout.closeDrawer(binding.navViewDrawer)
            itemHandled // Return true if the item was handled
        }



        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topAppBar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
            // You can also show/hide toolbar or change its title based on destination.
            // Example: if (destination.id == R.id.some_fragment_without_toolbar) supportActionBar?.hide() else supportActionBar?.show()
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
        // This will handle the "up" button (back arrow) in the AppBar,
        // as well as the drawer icon (hamburger icon) if linked with AppBarConfiguration.
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    // This is for the toolbar menu on the right (if you have one, e.g., settings icon)
    // Your current R.id.action_toolbar_menu seems to be intended to open the drawer.
    // The hamburger/back icon for the drawer is typically handled by onSupportNavigateUp().
    // If R.menu.main_toolbar_menu is indeed for an options menu in the app bar, this is fine.
    // If action_toolbar_menu is *another* button to open the drawer, it's redundant with the hamburger.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle clicks on items in the options menu (top app bar action items)
        // The R.id.action_toolbar_menu in your code seems to be trying to open the drawer.
        // The hamburger icon (handled by onSupportNavigateUp) usually does this.
        // If you have a separate button in the toolbar *in addition* to the hamburger icon:
        return when (item.itemId) {
            R.id.action_toolbar_menu -> { // Assuming this ID is from main_toolbar_menu.xml
                // This logic to toggle the drawer with a specific button in the toolbar is fine if intended.
                if (binding.drawerLayout.isDrawerOpen(binding.navViewDrawer)) {
                    binding.drawerLayout.closeDrawer(binding.navViewDrawer)
                } else {
                    binding.drawerLayout.openDrawer(binding.navViewDrawer)
                }
                true
            }
            // Handle other action bar items here
            else -> NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        // Close the drawer if it's open when the back button is pressed
        if (binding.drawerLayout.isDrawerOpen(binding.navViewDrawer)) {
            binding.drawerLayout.closeDrawer(binding.navViewDrawer)
        } else {
            super.onBackPressed()
        }
    }
}