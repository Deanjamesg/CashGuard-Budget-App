// BudgetViewModel.kt
package com.example.cashguard.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Repository.BudgetRepository
import com.example.cashguard.data.Budget
import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {
    fun createBudget(budget: Budget) = viewModelScope.launch {
        repository.insertBudget(budget)
    }

    fun updateBudget(budget: Budget) = viewModelScope.launch {
        repository.updateBudget(budget)
    }

    fun deleteBudget(budget: Budget) = viewModelScope.launch {
        repository.deleteBudget(budget)
    }

    suspend fun getMonthlyBudget(userId: Int, month: String): Budget? {
        return repository.getBudgetByMonth(userId, month)
    }

    suspend fun getAllBudgets(userId: Int) = repository.getBudgets(userId)
}

class BudgetViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}