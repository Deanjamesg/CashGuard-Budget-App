package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Category


@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getCategoriesByUser(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type")
    suspend fun getCategoriesByType(userId: Int, type: String): List<Category>
}