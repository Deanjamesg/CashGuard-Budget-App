package com.example.cashguard.Activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cashguard.databinding.ActivityBudgetManagerBinding

class BudgetManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityBudgetManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_budget_manager)

        Log.d("Button", "Budget Manager Activity")

    }
}