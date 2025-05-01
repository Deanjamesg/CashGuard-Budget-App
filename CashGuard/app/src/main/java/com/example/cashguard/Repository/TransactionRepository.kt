package com.example.cashguard.Repository

import com.example.cashguard.Dao.TransactionDao
import com.example.cashguard.data.Transaction
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    suspend fun getTransactionsByUser(userId: Int) = transactionDao.getTransactionsByUser(userId)
    suspend fun getTransactionsByType(userId: Int, type: String) =
        transactionDao.getTransactionsByType(userId, type)
    suspend fun deleteTransaction(transactionId: Int) =
        transactionDao.deleteTransaction(transactionId)

    suspend fun getByDateRange(userId: Int, from: Date, to: Date): List<Transaction> =
        transactionDao.getDateRange(userId, from, to)
}