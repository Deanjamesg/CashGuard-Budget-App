package com.example.cashguard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "categories",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],  // Matches User entity's primary key
        childColumns = ["user_id"],  // Matches Category's column name
        onDelete = ForeignKey.CASCADE
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    @ColumnInfo(name = "user_id", index = true)
    val userId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "budget_amount")
    val budgetAmount: Double? = null
)