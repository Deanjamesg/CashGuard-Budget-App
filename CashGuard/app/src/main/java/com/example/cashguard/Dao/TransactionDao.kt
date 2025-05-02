//source: https://developer.android.com/training/data-storage/room/accessing-data#:~:text=When%20you%20use%20the%20Room,the%20DAOs%20that%20you%20define.
//title: Accessing data using Room DAOs
//Author: Android Developers
//Date Accessed: 2 May 2025

package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Transaction
import java.util.Date

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    suspend fun getTransactionsByUser(userId: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = :type ORDER BY date DESC")
    suspend fun getTransactionsByType(userId: Int, type: String): List<Transaction>

    @Query("DELETE FROM transactions WHERE transactionId = :transactionId")
    suspend fun deleteTransaction(transactionId: Int)

    // ─── New: suspend call for searching by Date range ──────────────────────
    @Query("""SELECT * FROM transactions WHERE user_id = :userId AND date BETWEEN :fromDate AND :toDate ORDER BY date DESC""")
    suspend fun getDateRange(userId:   Int, fromDate: Date, toDate: Date): List<Transaction>

    @Query("""SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND category_name = :categoryName AND type = 'Expense'""")
        suspend fun getSumExpensesByCategoryName(userId: Int, categoryName: String): Double? // Nullable
}