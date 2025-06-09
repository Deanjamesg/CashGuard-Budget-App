package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.ExpenseBar
import com.example.cashguard.data.SearchTransactionItem
import com.example.cashguard.data.Transaction
import java.util.Date

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    suspend fun getTransactionsByUser(userId: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = :type ORDER BY date DESC")
    suspend fun getTransactionsByType(userId: String, type: String): List<Transaction>

    @Query("DELETE FROM transactions WHERE transactionId = :transactionId")
    suspend fun deleteTransaction(transactionId: String): Int

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND date BETWEEN :fromDate AND :toDate ORDER BY date DESC")
    suspend fun getDateRange(userId: String, fromDate: Date, toDate: Date): List<Transaction>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND category_id = :categoryId AND type = 'Expense' AND date BETWEEN :startDate AND :endDate")
    suspend fun getSumExpensesByCategoryIdAndDateRange(userId: String, categoryId: String, startDate: Date, endDate: Date): Double?

    @Query("""
    SELECT
        t.transactionId,
        c.name AS categoryName,
        t.note AS title,
        t.type,
        t.amount,
        t.date
    FROM
        transactions t
    LEFT JOIN
        categories c ON t.category_id = c.categoryId
    WHERE
        t.user_id = :userId AND t.date BETWEEN :fromDate AND :toDate
    ORDER BY
        t.date DESC
""")
    suspend fun getTransactionsByDateRange(userId: String, fromDate: Date, toDate: Date): List<SearchTransactionItem>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND category_id = :categoryId AND type = 'Expense'")
    suspend fun getSumExpensesByCategoryId(userId: String, categoryId: String): Double?

    @Query("""
        SELECT c.name AS categoryName, c.color AS categoryColor, SUM(t.amount) AS totalAmount 
        FROM transactions t
        JOIN categories c ON t.category_id = c.categoryId
        WHERE t.type = 'Expense' AND t.user_id = :userId 
        GROUP BY c.name, c.color 
        ORDER BY totalAmount DESC
    """)
    suspend fun getTransactionsExpenseBar(userId: String): List<ExpenseBar>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'Expense'")
    suspend fun getTotalExpenses(userId: String): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'Income'")
    suspend fun getTotalIncome(userId: String): Double?

    @Query("""
    SELECT
        t.transactionId,
        c.name AS categoryName,
        t.note AS title,
        t.type,
        t.amount,
        t.date
    FROM
        transactions t
    LEFT JOIN
        categories c ON t.category_id = c.categoryId
    WHERE
        t.user_id = :userId AND t.date BETWEEN :fromDate AND :toDate AND t.type = :type
    ORDER BY
        t.date DESC
""")
    suspend fun getTransactionsByTypeAndDateRange(userId: String, type: String, fromDate: Date, toDate: Date): List<SearchTransactionItem>
}