package com.example.cashguard.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.BudgetRepository
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.ExpenseBar
import kotlinx.coroutines.launch

class BudgetFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val budgetRepository: BudgetRepository
    private val sessionManager: SessionManager

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _expenseData = MutableLiveData<List<ExpenseBar>>()
    val expenseData: LiveData<List<ExpenseBar>> = _expenseData

    private val _budgetData = MutableLiveData<Double?>()
    val budgetData: LiveData<Double?> = _budgetData

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)

        val budgetDao = AppDatabase.getInstance(application).budgetDao()
        budgetRepository = BudgetRepository(budgetDao)

        sessionManager = SessionManager(application)

        fetchUserData()
    }

    private fun fetchUserData() {

        viewModelScope.launch {
            _isLoading.postValue(true)
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                try {
                    val expenses = transactionRepository.getTransactionsExpenseBar(userId)
                    _expenseData.postValue(expenses)

                    val currentMonth = java.text.SimpleDateFormat("MMM-yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date())

                    val budget = budgetRepository.getBudgetTotalByMonth(userId, currentMonth)

                    if (budget != null) {
                        _budgetData.postValue(budget)
                    } else {
                        _budgetData.postValue(0.0)
                    }
                } catch (e: Exception) {
                    _expenseData.postValue(emptyList())
                    _budgetData.postValue(0.0)
                }
            } else {
                _expenseData.postValue(emptyList())
                _budgetData.postValue(0.0)
            }
            _isLoading.postValue(false)
        }
    }

    fun refreshData() {
        fetchUserData()
    }
}