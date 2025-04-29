package com.example.cashguard.Repository

import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.data.Category

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    suspend fun getCategories(userId: Int) = categoryDao.getCategoriesByUser(userId)
    suspend fun getCategoriesByType(userId: Int, type: String) = categoryDao.getCategoriesByType(userId, type)
}