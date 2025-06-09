package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Category
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.data.ProgressBar

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getCategoriesByUser(userId: String): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Expense'")
    suspend fun getExpenseCategoriesByUser(userId: String): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = :type")
    suspend fun getCategoriesByType(userId: String, type: String): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Expense' AND max_goal IS NOT NULL")
    suspend fun getBudgetCategoriesByUser(userId: String): List<Category>

    @Query("SELECT * FROM categories WHERE user_id = :userId AND type = 'Income'")
    suspend fun getIncomeCategoriesByUser(userId: String): List<Category>

    @Query("SELECT name FROM categories WHERE type = 'Expense' AND user_id = :userId")
    suspend fun getExpenseCategoryNames(userId: String): List<String>

    @Query("SELECT categoryId, name, type FROM categories WHERE user_id = :userId AND is_active = 1")
    suspend fun getSpinnerCategories(userId: String): List<CategoryItem>?

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: String): Category

    @Query("SELECT * FROM categories WHERE user_id = :userId AND is_active = 1")
    suspend fun getActiveCategoriesByUser(userId: String): List<Category>

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM categories WHERE categoryId = :categoryId AND user_id = :userId")
    suspend fun deleteByIdAndUserId(categoryId: String, userId: String): Int

    @Transaction
    @Query(
        """
    SELECT
        c.name AS categoryName,
        COALESCE(SUM(t.amount), 0.0) AS totalSpentAmount,
        c.max_goal AS maxBudgetAmount,
        c.min_goal AS minBudgetAmount
    FROM
        categories c
    LEFT JOIN 
        transactions t ON c.categoryId = t.category_id AND c.user_id = t.user_id
                      AND t.type = 'Expense'
    WHERE
        c.user_id = :userId AND c.max_goal IS NOT NULL
    GROUP BY
        c.name, c.max_goal, c.min_goal
    ORDER BY
        c.name ASC
"""
    )
    suspend fun getProgressBarData(userId: String): List<ProgressBar>
}