package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Budget

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets WHERE user_id = :userId")
    suspend fun getBudgetsByUser(userId: Int): List<Budget>

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND financial_month = :month")
    suspend fun getBudgetByMonth(userId: Int, month: String): Budget?

    @Query("DELETE FROM budgets WHERE budgetId = :budgetId")
    suspend fun deleteBudget(budgetId: Int)
}