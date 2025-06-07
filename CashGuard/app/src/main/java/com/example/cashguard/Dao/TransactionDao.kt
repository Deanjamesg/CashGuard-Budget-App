package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.data.SearchTransactionItem
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
    suspend fun getDateRange(userId: Int, fromDate: Date, toDate: Date): List<Transaction>

    @Query("SELECT transactionId, type, category_name AS categoryName, amount, date FROM transactions WHERE user_id = :userId AND date BETWEEN :fromDate AND :toDate ORDER BY date DESC")
    suspend fun getTransactionsByDateRange(userId: Int, fromDate: Date, toDate: Date): List<SearchTransactionItem>

    @Query("""SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND category_name = :categoryName AND type = 'Expense'""")
    suspend fun getSumExpensesByCategoryName(userId: Int, categoryName: String): Double? // Nullable

    @Query("""SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = :transactionType AND date BETWEEN :fromDate AND :toDate""")
    suspend fun getTotalAmountByTypeAndDateRange(
        userId: Int,
        transactionType: String,
        fromDate: Date,
        toDate: Date
    ): Double?

    @Query("SELECT c.name AS categoryName, c.color AS categoryColor, SUM(t.amount) AS totalAmount FROM categories c " +
            "JOIN transactions t ON c.name = t.category_name AND c.user_id = t.user_id WHERE t.type = 'Expense' AND t.user_id = :userId " +
            "GROUP BY c.name, c.color ORDER BY totalAmount DESC")
    suspend fun getTransactionsExpenseBar(userId: Int): List<ExpenseBar>

    // --- New Query: Get total expenses for a user ---
    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'Expense'")
    suspend fun getTotalExpenses(userId: Int): Double?

    // --- New Query: Get total income for a user ---
    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'Income'")
    suspend fun getTotalIncome(userId: Int): Double?

    @Query("SELECT transactionId, type ,category_name AS categoryName, amount, date FROM transactions WHERE user_id = :userId AND date BETWEEN :fromDate AND :toDate AND type = :type ORDER BY date DESC")
    suspend fun getTransactionsByTypeAndDateRange(userId: Int, type: String, fromDate: Date, toDate: Date): List<SearchTransactionItem>
}