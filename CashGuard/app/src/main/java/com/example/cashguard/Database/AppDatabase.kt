package com.example.cashguard.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cashguard.Adapter.Converters
import com.example.cashguard.Dao.BudgetDao
import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.Dao.TransactionDao
import com.example.cashguard.Dao.UserDao
import com.example.cashguard.data.Budget
import com.example.cashguard.data.Category
import com.example.cashguard.data.Transaction
import com.example.cashguard.data.User

@Database(entities = [User::class, Category::class, Transaction::class, Budget::class], version = 6)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cashguard_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}