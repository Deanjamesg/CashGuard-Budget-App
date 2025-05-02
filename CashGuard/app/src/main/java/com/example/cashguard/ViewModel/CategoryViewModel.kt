package com.example.cashguard.ViewModel

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
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    companion object {
        private val DEFAULT_CATEGORIES = listOf(
            "Food & Drink" to "Expense",
            "Transport" to "Expense",
            "Household" to "Expense",
            "Entertainment" to "Expense",
            "Subscription" to "Expense",
            "Salary" to "Income"
        )
    }

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        repository = CategoryRepository(categoryDao)
    }

    fun initializeUserCategories(userId: Int) = viewModelScope.launch {
        if (repository.getCategories(userId).isEmpty()) {
            createDefaultCategories(userId)
        }
        loadCategories(userId)
    }

     fun createDefaultCategories(userId: Int) = viewModelScope.launch {
        val defaultCategories = DEFAULT_CATEGORIES.map { (name, type) ->
            Category(userId = userId, name = name, type = type)
        }
        repository.insertCategories(defaultCategories)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
        loadCategories(category.userId)
    }

    fun loadCategories(userId: Int) = viewModelScope.launch {
        _categories.postValue(repository.getCategories(userId))
    }

    fun getRemovableCategories(): List<Category> {
        return _categories.value?.filter { category ->
            !isDefaultCategory(category)
        } ?: emptyList()
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        if (isDefaultCategory(category)) {
            _errorMessage.postValue("Cannot delete default category")
            return@launch
        }
        repository.deleteCategory(category)
        loadCategories(category.userId)
    }

    fun isDefaultCategory(category: Category): Boolean {
        return DEFAULT_CATEGORIES.any { it.first == category.name }
    }

    suspend fun getExpenseCategories(userId: Int): List<Category> {
        return repository.getExpenseCategories(userId)
    }
}