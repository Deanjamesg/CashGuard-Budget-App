package com.example.cashguard.Dao

import androidx.room.*
import com.example.cashguard.data.Category
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.data.ProgressBar
import java.util.Date


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

    @Query("SELECT categoryId, name, type FROM categories WHERE user_id = :userId")
    suspend fun getCategorySpinnerByUser(userId: Int): List<CategoryItem>

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM categories WHERE categoryId = :categoryId AND user_id = :userId")
    suspend fun deleteByIdAndUserId(categoryId: Int, userId: Int): Int

    @Transaction
    @Query(
        """
        SELECT
            c.name AS categoryName,
            c.budget_amount AS budgetAmount,
            COALESCE(SUM(t.amount), 0.0) AS expenseAmount
        FROM
            categories c
        LEFT JOIN 
            transactions t ON c.name = t.category_name AND c.user_id = t.user_id 
                          AND t.type = 'Expense'
        WHERE
            c.user_id = :userId AND c.budget_amount IS NOT NULL
        GROUP BY
            c.name, c.budget_amount
        ORDER BY
            c.name ASC
    """
    )
    suspend fun getProgressBarData(userId: Int): List<ProgressBar>
}