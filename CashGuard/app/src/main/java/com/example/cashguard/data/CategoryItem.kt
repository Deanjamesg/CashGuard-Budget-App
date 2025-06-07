package com.example.cashguard.data

data class CategoryItem(
    val categoryId: Int,
    val name: String,
    val type: String
)

//data class CategoryItem(
//    @ColumnInfo(name = "categoryId") val categoryId: Int,
//    @ColumnInfo(name = "name") val name: String,
//    @ColumnInfo(name = "type") val type: String
//)