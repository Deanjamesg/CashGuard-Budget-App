package com.example.cashguard.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.ExpenseBar
import kotlinx.coroutines.launch

class ExpenseFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val sessionManager: SessionManager

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _expenseBarData = MutableLiveData<List<ExpenseBar>>()
    val expenseBarData: LiveData<List<ExpenseBar>> = _expenseBarData

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        sessionManager = SessionManager(application)

        fetchExpenseBars()
    }

    private fun fetchExpenseBars() {

        viewModelScope.launch {
            _isLoading.postValue(true)
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                try {
                    val data = transactionRepository.getTransactionsExpenseBar(userId)
                    _expenseBarData.postValue(data)
                } catch (e: Exception) {
                    _expenseBarData.postValue(emptyList())
                }
            } else {
                _expenseBarData.postValue(emptyList())
            }
            _isLoading.postValue(false)
        }
    }

    fun refreshExpenseBars() {
        fetchExpenseBars()
    }

}