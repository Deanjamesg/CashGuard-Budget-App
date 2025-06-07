package com.example.cashguard.Repository

import android.graphics.Color
import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.data.Category
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.data.ProgressBar
import java.util.Date

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    suspend fun getUserCategories(userId: Int) = categoryDao.getCategoriesByUser(userId)

    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun getBudgetCategories(userId: Int) = categoryDao.getBudgetCategoriesByUser(userId)

    suspend fun getCategoriesByType(userId: Int, type: String) = categoryDao.getCategoriesByType(userId, type)

    suspend fun getCategorySpinner(userId: Int): List<CategoryItem> =
        categoryDao.getCategorySpinnerByUser(userId)

    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    suspend fun insertCategories(categories: List<Category>) {
        for (category in categories) {
            categoryDao.insert(category)
        }
    }
    suspend fun getExpenseCategories(userId: Int): List<Category> {
        return categoryDao.getExpenseCategoriesByUser(userId)
    }

    suspend fun getIncomeCategories(userId: Int): List<Category> {
        return categoryDao.getIncomeCategoriesByUser(userId)
    }

    suspend fun addCategoryByNameAndType(name: String, type: String, userId: Int, colorValue: Int = Color.BLACK) {
        val newCategory = Category(userId = userId, name = name, type = type, budgetAmount = null, color = colorValue)
        categoryDao.insert(newCategory)
    }

    suspend fun deleteCategoryById(categoryId: Int, userId: Int): Boolean {
        val rowsAffected = categoryDao.deleteByIdAndUserId(categoryId, userId)
        return rowsAffected > 0
    }

    suspend fun getProgressBarData(userId: Int): List<ProgressBar> {
        return categoryDao.getProgressBarData(userId)
    }

}