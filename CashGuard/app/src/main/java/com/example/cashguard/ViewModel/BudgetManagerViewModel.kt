package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.BudgetRepository
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.data.Budget
import com.example.cashguard.data.Category
import kotlinx.coroutines.launch
import java.util.Locale

class BudgetManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository: CategoryRepository
    private val budgetRepository: BudgetRepository
    private val sessionManager: SessionManager
    private val userId: Int

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _budget = MutableLiveData<Budget>()
    val budget: LiveData<Budget> = _budget

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)

        val budgetDao = AppDatabase.getInstance(application).budgetDao()
        budgetRepository = BudgetRepository(budgetDao)

        sessionManager = SessionManager(application)
        userId = sessionManager.getUserId()

        if (userId != -1) {
            loadUserCategories()
        } else {
            _categories.postValue(emptyList())
        }
    }

    private fun loadUserCategories() {
        viewModelScope.launch {
            val expenseCategories = categoryRepository.getExpenseCategories(userId)
            _categories.postValue(expenseCategories)

            val currentMonth = java.text.SimpleDateFormat("MMM-yyyy", Locale.getDefault())
                .format(java.util.Date())
            val userBudget = budgetRepository.getBudgetByMonth(userId, currentMonth)
            _budget.postValue(userBudget)
        }
    }

    fun saveBudgetOnClick(categories : List<Category>, budgetTotal: Double) {
        viewModelScope.launch {
            val newBudget = Budget(
                budgetId = budget.value.budgetId,
                userId = userId,
                financialMonth = budget.value.financialMonth,
                budgetAmount = budgetTotal
            )
            budgetRepository.updateBudget(newBudget)

            for (category in categories) {
                categoryRepository.updateCategory(category)
            }
            loadUserCategories()
        }
    }

}