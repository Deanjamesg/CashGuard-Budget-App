package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Transaction

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
}