//source: https://developer.android.com/training/data-storage/room/accessing-data#:~:text=When%20you%20use%20the%20Room,the%20DAOs%20that%20you%20define.
//title: Accessing data using Room DAOs
//Author: Android Developers
//Date Accessed: 2 May 2025

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

    @Delete
    suspend fun delete(category: Category)
}