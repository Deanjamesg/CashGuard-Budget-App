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
import java.util.Calendar
import java.util.Locale

class BudgetManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository: CategoryRepository
    private val budgetRepository: BudgetRepository
    private val sessionManager: SessionManager
    private val userId: String

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

        if (userId != "-1") {
            loadUserCategories()
        } else {
            _categories.postValue(emptyList())
        }
    }

    private fun loadUserCategories() {
        viewModelScope.launch {

            val budget = budgetRepository.getCurrentBudget(userId)
            if (budget != null) {
                val expenseCategories = categoryRepository.getActiveExpenseCategoriesByBudgetId(budget.budgetId)
                _categories.postValue(expenseCategories)
                _budget.postValue(budget)
            } else {

                Log.w("BudgetManagerVM", "No current budget found for user $userId")
                _categories.postValue(emptyList())
            }
        }
    }

    fun saveBudgetOnClick(categories : List<Category>, budgetTotal: Double) {
        viewModelScope.launch {
            val currentBudget = budget.value ?: return@launch

            val newBudget = Budget(
                budgetId = currentBudget.budgetId,
                userId = userId,
                startDate = currentBudget.startDate,
                endDate = currentBudget.endDate,
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