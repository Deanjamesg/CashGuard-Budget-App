package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.data.CategoryItem // Ensure CategoryItem has color if you intend to display it
import kotlinx.coroutines.launch

class CategoryManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository: CategoryRepository
    private val sessionManager: SessionManager
    private val userId: String

    private var userCategoryObjects: List<CategoryItem> = emptyList()

    private val _expenseCategories = MutableLiveData<List<CategoryItem>>()
    val expenseCategories: LiveData<List<CategoryItem>> = _expenseCategories

    private val _incomeCategories = MutableLiveData<List<CategoryItem>>()
    val incomeCategories: LiveData<List<CategoryItem>> = _incomeCategories

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        sessionManager = SessionManager(application)
        userId = sessionManager.getUserId()

        if (userId != "-1") {
            loadUserCategories()
        } else {
            Log.e("CategoryVM", "User ID not found in init, cannot load categories immediately.")
            _expenseCategories.postValue(emptyList())
            _incomeCategories.postValue(emptyList())
        }
    }

    private fun loadUserCategories() {
        if (userId == "-1") {
            Log.e("CategoryVM", "User ID not found, cannot load categories.")
            _expenseCategories.postValue(emptyList())
            _incomeCategories.postValue(emptyList())
            return
        }
        viewModelScope.launch {
            userCategoryObjects = categoryRepository.getCategorySpinner(userId)
            Log.d("CategoryVM", "Loaded ${userCategoryObjects.size} categories for user $userId")
            processAndPostCategories()
        }
    }

    private fun processAndPostCategories() {
        val expenseList = userCategoryObjects
            .filter { it.type.equals("Expense", ignoreCase = true) }
        _expenseCategories.postValue(expenseList)
        Log.d("CategoryVM", "Posted ${expenseList.size} expense categories.")

        val incomeList = userCategoryObjects
            .filter { it.type.equals("Income", ignoreCase = true) }
        _incomeCategories.postValue(incomeList)
        Log.d("CategoryVM", "Posted ${incomeList.size} income categories.")
    }

    fun addCategory(name: String, type: String, color: Int) {
        if (userId == "-1") {
            Log.e("CategoryVM", "Cannot add category, user ID not found.")
            return
        }
        if (name.isBlank()) {
            Log.w("CategoryVM", "Category name cannot be blank.")
            return
        }

        viewModelScope.launch {
            try {
                categoryRepository.addCategoryByNameAndType(name, type, userId, color)
                Log.d("CategoryVM", "Attempted to add category: $name, Type: $type, Color: $color for user $userId")
                loadUserCategories()
            } catch (e: Exception) {
                Log.e("CategoryVM", "Error adding category: $name", e)
            }
        }
    }

    fun deleteCategory(category: CategoryItem) {
        if (userId == "-1") {
            Log.e("CategoryVM", "Cannot delete category, user ID not found.")
            return
        }
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategoryById(category.categoryId, userId)
                Log.d("CategoryVM", "Attempted to delete category ID: ${category.categoryId} for user $userId")
                loadUserCategories()
            } catch (e: Exception) {
                Log.e("CategoryVM", "Error deleting category: ${category.name}", e)
            }
        }
    }
}