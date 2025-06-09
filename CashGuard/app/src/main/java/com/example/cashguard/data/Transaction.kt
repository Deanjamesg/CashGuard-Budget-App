package com.example.cashguard.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey
    val transactionId: String,

    @ColumnInfo(name = "user_id", index = true)
    val userId: String,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "photo_filename")
    val photoFilename: String? = null,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "category_id", index = true)
    val categoryId: String
)