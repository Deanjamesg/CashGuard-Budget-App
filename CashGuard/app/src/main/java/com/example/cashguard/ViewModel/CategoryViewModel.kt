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
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.ViewModel.BudgetInfo
import com.example.cashguard.R


class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val transactionRepository: TransactionRepository
    private val _budgetInfoList = MutableLiveData<List<BudgetInfo>>()
    val budgetInfoList: LiveData<List<BudgetInfo>> = _budgetInfoList

    /*companion object {
        private val DEFAULT_CATEGORIES = listOf(
            "Food & Drink" to "Expense",
            "Transport" to "Expense",
            "Household" to "Expense",
            "Entertainment" to "Expense",
            "Subscription" to "Expense",
            "Salary" to "Income"
        )
    }*/

    companion object {
        // Store Name, Type, and Color Resource ID using Triple
        private val DEFAULT_CATEGORIES = listOf(
            Triple("Food & Drink", "Expense", R.color.red),
            Triple("Transport", "Expense", R.color.lightBlue),
            Triple("Household", "Expense", R.color.yellow),
            Triple("Entertainment", "Expense", R.color.violet),
            Triple("Subscription", "Expense", R.color.green),
            Triple("Salary", "Income", R.color.stroke)

        )
    }

    init {
        val db = AppDatabase.getInstance(application)
        val transactionDao = db.transactionDao()
        val categoryDao = db.categoryDao()
        transactionRepository = TransactionRepository(transactionDao)
        repository = CategoryRepository(categoryDao)
    }

    fun initializeUserCategories(userId: Int) = viewModelScope.launch {
            if (repository.getCategories(userId).isEmpty()) {
                Log.d("category creation","initial")
                createDefaultCategories(userId)
            }
            loadCategories(userId)
    }


    fun createDefaultCategories(userId: Int) = viewModelScope.launch {
        // Get context to resolve color resource IDs
        val context = getApplication<Application>().applicationContext
        Log.d("category creation","create")
        val defaultCategories = DEFAULT_CATEGORIES.map { (name, type, colorResId) ->
            // Create Category object, explicitly setting null budget and resolving color
            Category(
                userId = userId,
                name = name,
                type = type,
                budgetAmount = null, // Explicitly set budgetAmount to null
                color = ContextCompat.getColor(context, colorResId) // Resolve color ID
            )
        }
        Log.d("CategoryViewModel", "Creating ${defaultCategories.size} default categories.")
        repository.insertCategories(defaultCategories)
    }

     /*fun createDefaultCategories(userId: Int) = viewModelScope.launch {
        val defaultCategories = DEFAULT_CATEGORIES.map { (name, type) ->
            Category(userId = userId, name = name, type = type)
        }
        repository.insertCategories(defaultCategories)
    }*/

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

    fun loadBudgetInfo(userId: Int) = viewModelScope.launch {
        Log.d("CategoryViewModel", "loadBudgetInfo called for userId: $userId")
        val budgetCategories = repository.getBudgetCategories(userId) // Uses new repo method
        Log.d("CategoryViewModel", "Fetched ${budgetCategories.size} budget categories")
        val budgetInfoResult = budgetCategories.map { category ->
            val budgetAmt = category.budgetAmount ?: 0.0 // Should be non-null from query
            val spentAmt = transactionRepository.getSpentAmountForCategory(userId, category.name) ?: 0.0
            Log.d("CategoryViewModel", "Mapping -> Name: ${category.name}, Budget: ${budgetAmt}, Spent: ${spentAmt}, Color: ${category.color}")
            BudgetInfo(
                categoryId = category.categoryId,
                name = category.name,
                budgetAmount = budgetAmt,
                spentAmount = spentAmt,
                color = category.color,
                userId = category.userId
            )
        }
        _budgetInfoList.postValue(budgetInfoResult)
        Log.d("CategoryViewModel", "Posted list of ${budgetInfoResult.size} BudgetInfo to LiveData")
    }

    fun addBudgetCategory(name: String, budgetAmount: Double, color: Int, userId: Int) = viewModelScope.launch {
        Log.d("CategoryViewModel", "Saving Budget -> Name: $name, Amount: $budgetAmount, ColorInt: $color, UserID: $userId")
        val newCategory = Category(
            userId = userId,
            name = name.trim(),
            type = "Expense",
            budgetAmount = budgetAmount, // Set the budget amount
            color = color
            // categoryId will be auto-generated or replaced by OnConflictStrategy
        )
        repository.insertCategory(newCategory) // Uses INSERT OR REPLACE
        loadBudgetInfo(userId) // Refresh the list
    }

    suspend fun getExpenseCategories(userId: Int): List<Category> {
        return repository.getExpenseCategories(userId)
    }

    suspend fun getIncomeCategories(userId: Int): List<Category> {
        return repository.getIncomeCategories(userId)
    }
}