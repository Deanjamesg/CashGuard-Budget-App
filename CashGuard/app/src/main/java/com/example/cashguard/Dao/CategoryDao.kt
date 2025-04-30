package com.example.cashguard.Dao

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cashguard.data.Category


@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Expense'")
    suspend fun getExpenseCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type")
    suspend fun getCategoriesByType(userId: Int, type: String): List<Category>

    @Delete
    suspend fun delete(category: Category)
}