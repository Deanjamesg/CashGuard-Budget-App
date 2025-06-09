package com.example.cashguard.data

data class ProgressBar (
    val categoryName: String,
    val totalSpentAmount: Double,
    val maxBudgetAmount: Double,
    val minBudgetAmount: Double
)