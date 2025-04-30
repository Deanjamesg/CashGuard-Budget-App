package com.example.cashguard.Model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Transaction
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        repository = TransactionRepository(transactionDao)
    }

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(transactionId: Int) = viewModelScope.launch {
        repository.deleteTransaction(transactionId)
    }
}