package com.example.cashguard.Repository

import com.example.cashguard.Dao.TransactionDao
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.data.SearchTransactionItem
import com.example.cashguard.data.Transaction
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {

    private val transactionDatabaseReference = FirebaseDatabase.getInstance().getReference("transactions")

    suspend fun insertTransaction(userId: String, categoryId: String, date: Date,
                                  amount: Double, type: String, note: String?, photoFileName: String?): String {

        val firebaseKey = transactionDatabaseReference.push().key
            ?: throw Exception("Could not generate a unique key from Firebase for Transaction.")

        val transaction = Transaction(
            transactionId = firebaseKey,
            userId = userId,
            date = date,
            amount = amount,
            note = note,
            photoFilename = photoFileName,
            type = type,
            categoryId = categoryId
        )

        transactionDatabaseReference.child(firebaseKey).setValue(transaction).await()

        transactionDao.insert(transaction)

        return firebaseKey
    }

    suspend fun getTransactionsByUser(userId: String): List<Transaction> = transactionDao.getTransactionsByUser(userId)

    suspend fun getTransactionsByType(userId: String, type: String): List<Transaction> =
        transactionDao.getTransactionsByType(userId, type)

    suspend fun deleteTransaction(transactionId: String) {
        val rowsAffected = transactionDao.deleteTransaction(transactionId)
        if (rowsAffected > 0) {
            transactionDatabaseReference.child(transactionId).removeValue().await()
        }
    }

    suspend fun getSumExpensesByCategoryIdAndDateRange(userId: String, categoryId: String, startDate: Date, endDate: Date): Double? {
        return transactionDao.getSumExpensesByCategoryIdAndDateRange(userId, categoryId, startDate, endDate)
    }

    suspend fun getTransactionById(transactionId: String): Transaction? {
        return transactionDao.getTransactionById(transactionId)
    }

    suspend fun getByDateRange(userId: String, from: Date, to: Date): List<Transaction> =
        transactionDao.getDateRange(userId, from, to)

    suspend fun getTransactionsByDateRange(userId: String, from: Date, to: Date): List<SearchTransactionItem> =
        transactionDao.getTransactionsByDateRange(userId, from, to)

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