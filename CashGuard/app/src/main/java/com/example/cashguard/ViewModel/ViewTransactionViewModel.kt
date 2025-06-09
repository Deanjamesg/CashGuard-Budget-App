package com.example.cashguard.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.Transaction
import kotlinx.coroutines.launch

class ViewTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository
    private val sessionManager: SessionManager

    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    private val _categoryName = MutableLiveData<String?>()
    val categoryName: LiveData<String?> = _categoryName

    init {
        val db = AppDatabase.getInstance(application)
        transactionRepository = TransactionRepository(db.transactionDao())
        categoryRepository = CategoryRepository(db.categoryDao())
        sessionManager = SessionManager(application)
    }

    fun loadTransactionDetails(transactionId: String) {
        viewModelScope.launch {
            val fetchedTransaction = transactionRepository.getTransactionById(transactionId)
            _transaction.postValue(fetchedTransaction)

            // If the transaction was found, fetch its category name
            fetchedTransaction?.let {
                val category = categoryRepository.getCategoryById(it.categoryId)
                _categoryName.postValue(category?.name)
            }
        }
    }
}