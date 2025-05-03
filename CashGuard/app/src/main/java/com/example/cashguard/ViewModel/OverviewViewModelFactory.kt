package com.example.cashguard.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.Repository.TransactionRepository

class OverviewViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val userId: Int // Pass the userId needed by the ViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverviewViewModel(transactionRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}