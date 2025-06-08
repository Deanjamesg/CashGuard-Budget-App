package com.example.cashguard.Repository

import com.example.cashguard.Dao.TransactionDao
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.data.SearchTransactionItem
import com.example.cashguard.data.Transaction
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    suspend fun getTransactionsByUser(userId: String) = transactionDao.getTransactionsByUser(userId)
    suspend fun getTransactionsByType(userId: String, type: String) =
        transactionDao.getTransactionsByType(userId, type)
    suspend fun deleteTransaction(transactionId: Int) =
        transactionDao.deleteTransaction(transactionId)

    suspend fun getByDateRange(userId: String, from: Date, to: Date): List<Transaction> =
        transactionDao.getDateRange(userId, from, to)

    suspend fun getTransactionsByDateRange(userId: String, from: Date, to: Date): List<SearchTransactionItem> =
        transactionDao.getTransactionsByDateRange(userId, from, to)

    suspend fun getSpentAmountForCategory(userId: String, categoryName: String): Double? {
        return transactionDao.getSumExpensesByCategoryName(userId, categoryName)
    }

    suspend fun getTransactionsExpenseBar(userId: String) : List<ExpenseBar> = transactionDao.getTransactionsExpenseBar(userId)

    suspend fun getTotalExpensesByUser(userId: String): Double? {
        return transactionDao.getTotalExpenses(userId)
    }

    suspend fun getTotalIncomeByUser(userId: String): Double? {
        return transactionDao.getTotalIncome(userId)
    }

    suspend fun getTransactionsByTypeAndDateRange(userId: String, type: String, fromDate: Date, toDate: Date): List<SearchTransactionItem> =
        transactionDao.getTransactionsByTypeAndDateRange(userId, type, fromDate, toDate)

}