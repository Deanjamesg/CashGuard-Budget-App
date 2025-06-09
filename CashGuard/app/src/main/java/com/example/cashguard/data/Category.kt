package com.example.cashguard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import android.graphics.Color

@Entity(
    tableName = "categories",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = Budget::class,
            parentColumns = ["budgetId"],
            childColumns = ["budget_id"],
            onDelete = ForeignKey.CASCADE
        ),]
)
data class Category(
    @PrimaryKey
    val categoryId: String,

    @ColumnInfo(name = "user_id", index = true)
    val userId: String,

    @ColumnInfo(name = "budget_id", index = true)
    val budgetId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "min_goal")
    val minGoal: Double?,

    @ColumnInfo(name = "max_goal")
    val maxGoal: Double?,

    @ColumnInfo(name = "is_active", defaultValue = "1")
    var isActive: Boolean = true,

    @ColumnInfo(name = "color", defaultValue = "-16777216")
    val color: Int = Color.BLACK
)