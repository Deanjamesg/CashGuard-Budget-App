package com.example.cashguard.Repository

import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.data.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    suspend fun getCategories(userId: Int) = categoryDao.getCategoriesByUser(userId)

    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun getBudgetCategories(userId: Int) = categoryDao.getBudgetCategoriesByUser(userId)

    //suspend fun getExpenseCategories(userId: Int) = categoryDao.getExpenseCategoriesByUser(userId)

    suspend fun getCategoriesByType(userId: Int, type: String) = categoryDao.getCategoriesByType(userId, type)

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

}