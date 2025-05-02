package com.example.cashguard.ViewModel

import android.graphics.Color

data class BudgetInfo(
    val categoryId: Int,
    val name: String,
    val budgetAmount: Double,
    val spentAmount: Double,
    val color: Int,
    val userId: Int
)
