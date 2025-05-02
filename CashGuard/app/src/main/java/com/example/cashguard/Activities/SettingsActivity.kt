package com.example.cashguard.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: ActivitySettingsBinding
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userId = sessionManager.getUserId()
        Log.d("SESSION", "Settings ID: ${sessionManager.getUserId()}")

        // Navigation handlers
        binding.homeIcon.setOnClickListener {
            Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }.also { startActivity(it) }
            finish()
        }

        binding.searchIcon.setOnClickListener {
            Intent(this, SearchByDateActivity::class.java).apply {
                putExtra("USER_ID", userId)
            }.also { startActivity(it) }
        }

        binding.helpButton.setOnClickListener {
            val intent = Intent(this, BudgetCalculatorActivity::class.java)
            startActivity(intent)
        }
    }
}