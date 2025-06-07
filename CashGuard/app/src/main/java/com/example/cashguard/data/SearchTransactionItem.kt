package com.example.cashguard.data

import java.util.Date

data class SearchTransactionItem(
    val transactionId: Int,
    val categoryName: String,
    val type : String,
    val amount: Double,
    val date: Date
)