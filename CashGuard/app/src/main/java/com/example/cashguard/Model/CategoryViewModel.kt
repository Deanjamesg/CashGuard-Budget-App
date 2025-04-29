package com.example.cashguard.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.data.Category
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        repository = CategoryRepository(categoryDao)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    suspend fun getCategories(userId: Int) = repository.getCategories(userId)

    suspend fun getCategoriesByType(userId: Int, type: String) =
        repository.getCategoriesByType(userId, type)

}