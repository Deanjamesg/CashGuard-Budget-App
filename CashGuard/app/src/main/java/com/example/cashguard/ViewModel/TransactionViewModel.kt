package com.example.cashguard.Model

import android.app.Application

import android.view.SurfaceControl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Transaction
import kotlinx.coroutines.launch
import java.util.Date

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    // Secondary constructor for Application-based initialization
    constructor(application: Application) : this(
        TransactionRepository(
            AppDatabase.getInstance(application).transactionDao()
        )
    )
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions


    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun deleteTransaction(transactionId: Int) = viewModelScope.launch {
        repository.deleteTransaction(transactionId)
    }

    suspend fun getTransactionsByDateRange(userId: Int, from: Date, to: Date) =
        repository.getByDateRange(userId, from, to)

    fun loadTransactionsByDateRange(userId: Int, from: Date, to: Date) {
        viewModelScope.launch {
            _transactions.value = repository.getByDateRange(userId, from, to)
        }
    }
}

// Factory class for creating instances of TransactionViewModel **
class TransactionViewModelFactory(private val repository: TransactionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}





