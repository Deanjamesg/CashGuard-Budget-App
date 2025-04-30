package com.example.cashguard.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.data.Category
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        repository = CategoryRepository(categoryDao)
    }

    fun getCategoriesByType(userId: Int, type: String): LiveData<List<Category>> {
        viewModelScope.launch {
            _categories.postValue(repository.getCategoriesByType(userId, type))
        }
        return _categories
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    suspend fun getCategories(userId: Int) = repository.getCategories(userId)

    fun loadCategories(userId: Int) = viewModelScope.launch {
        _categories.postValue(repository.getCategories(userId))
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
        // Refresh the list after deletion
        _categories.value?.let {
            loadCategories(category.userId)
        }
    }

}
