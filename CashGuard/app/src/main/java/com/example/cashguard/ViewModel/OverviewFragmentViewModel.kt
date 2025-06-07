package com.example.cashguard.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.TransactionRepository
import kotlinx.coroutines.launch

class OverviewFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val sessionManager: SessionManager

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _totalExpenses = MutableLiveData<Double>()
    val totalExpenses: LiveData<Double> = _totalExpenses

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        sessionManager = SessionManager(application)

        fetchUserDate()
    }

    private fun fetchUserDate() {
        val userId = sessionManager.getUserId()
        viewModelScope.launch {
            if (userId != -1) {
                val expenses = transactionRepository.getTotalExpensesByUser(userId)
                _totalExpenses.postValue(expenses ?: 0.0)
                val income = transactionRepository.getTotalIncomeByUser(userId)
                _totalIncome.postValue(income ?: 0.0)
            }
            _isLoading.postValue(false)
        }
    }

    fun refreshData() {
        _isLoading.postValue(true)
        fetchUserDate()
    }


}