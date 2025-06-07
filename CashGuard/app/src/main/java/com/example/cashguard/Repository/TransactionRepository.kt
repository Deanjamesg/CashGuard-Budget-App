package com.example.cashguard.Repository

import android.util.Log
import com.example.cashguard.Dao.TransactionDao
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.data.SearchTransactionItem
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

    suspend fun getTransactionsByDateRange(userId: Int, from: Date, to: Date): List<SearchTransactionItem> =
        transactionDao.getTransactionsByDateRange(userId, from, to)

    suspend fun getSpentAmountForCategory(userId: Int, categoryName: String): Double? {
        return transactionDao.getSumExpensesByCategoryName(userId, categoryName)
    }

    suspend fun getTransactionsExpenseBar(userId: Int) : List<ExpenseBar> = transactionDao.getTransactionsExpenseBar(userId)

    suspend fun getTotalAmountByTypeAndDate(userId: Int, transactionType: String, fromDate: Date, toDate: Date): Double? {
        // Log parameters being passed TO the DAO
        Log.d("PieChartDebug", "TransactionRepository: Calling DAO getTotalAmountByTypeAndDateRange for userId=$userId, type=$transactionType")

        // Call the DAO method
        val result = transactionDao.getTotalAmountByTypeAndDateRange(userId, transactionType, fromDate, toDate)

        // ** Log the result RECEIVED directly FROM the DAO **
        Log.d("PieChartDebug", "TransactionRepository: DAO returned result = $result for type=$transactionType")

        // Return the result
        return result
    }

    suspend fun getTotalExpensesByUser(userId: Int): Double? {
        return transactionDao.getTotalExpenses(userId)
    }

    suspend fun getTotalIncomeByUser(userId: Int): Double? {
        return transactionDao.getTotalIncome(userId)
    }

    suspend fun getTransactionsByTypeAndDateRange(userId: Int, type: String, fromDate: Date, toDate: Date): List<SearchTransactionItem> =
        transactionDao.getTransactionsByTypeAndDateRange(userId, type, fromDate, toDate)

}