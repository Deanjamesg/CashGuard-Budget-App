package com.example.cashguard.data

import java.util.Date

data class SearchTransactionItem(
    val transactionId: String,
    val categoryName: String,
    val note: String?,
    val type : String,
    val amount: Double,
    val date: Date
)