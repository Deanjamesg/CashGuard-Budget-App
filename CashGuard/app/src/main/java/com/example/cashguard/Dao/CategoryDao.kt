package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Category


@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Expense'")
    suspend fun getExpenseCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type")
    suspend fun getCategoriesByType(userId: Int, type: String): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Expense' AND budget_amount IS NOT NULL")
    suspend fun getBudgetCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Income'")
    suspend fun getIncomeCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT name FROM categories WHERE type = 'Expense' AND user_id = :userId")
    suspend fun getExpenseCategoryNames(userId: Int): List<String>

    @Delete
    suspend fun delete(category: Category)
}