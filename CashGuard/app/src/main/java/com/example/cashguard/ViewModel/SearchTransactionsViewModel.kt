package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.SearchTransactionItem
import kotlinx.coroutines.launch
import java.util.Date

class SearchTransactionsViewModel (application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val sessionManager: SessionManager

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _transactions = MutableLiveData<List<SearchTransactionItem>>()
    val transactions: LiveData<List<SearchTransactionItem>> = _transactions

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        sessionManager = SessionManager(application)

    }

    fun clearTransactions() {
        _transactions.value = emptyList()
    }

    fun searchTransactionsOnClick(type : String?, fromDate : Date?, toDate : Date?) {

        val userId = sessionManager.getUserId()

        if (fromDate == null || toDate == null) {
            _toastMessage.value = "Please select a From and To date."
            return
        }
        if (type == null || type == "All") {
            viewModelScope.launch {
                _transactions.postValue(
                    transactionRepository.getTransactionsByDateRange(userId, fromDate, toDate)
                )
            }
        } else if (type == "Income" || type == "Expense") {
            viewModelScope.launch {
                _transactions.postValue(
                    transactionRepository.getTransactionsByTypeAndDateRange(userId, type, fromDate, toDate)
                )
            }
        }



    }


}