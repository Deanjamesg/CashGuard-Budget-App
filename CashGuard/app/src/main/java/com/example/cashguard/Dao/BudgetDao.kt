package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Budget
import java.util.Date

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets WHERE user_id = :userId")
    suspend fun getAllBudgetsByUser(userId: String): List<Budget>

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND start_date = :startDate AND end_date = :endDate")
    suspend fun getUserBudgetByDateRange(userId: String, startDate: Date, endDate: Date): Budget

    @Query("SELECT budget_amount FROM budgets WHERE user_id = :userId AND budgetId = :budgetId")
    suspend fun getBudgetTotalByBudgetId(userId: String, budgetId: String): Double?

    @Query("SELECT * FROM budgets WHERE user_id = :userId AND :currentDate BETWEEN start_date AND end_date LIMIT 1")
    suspend fun getCurrentBudget(userId: String, currentDate: Date): Budget
}