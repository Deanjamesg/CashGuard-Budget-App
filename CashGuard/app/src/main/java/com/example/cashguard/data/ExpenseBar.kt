package com.example.cashguard.data

import androidx.room.ColumnInfo

data class ExpenseBar(
    val categoryName: String,
    val categoryColor: Int,
    val totalAmount: Double
)

//data class ExpenseBar(
//    @ColumnInfo(name = "categoryName")
//    val categoryName: String,
//
//    @ColumnInfo(name = "categoryColor")
//    val categoryColor: Int,
//
//    @ColumnInfo(name = "totalAmount")
//    val totalAmount: Double
//)