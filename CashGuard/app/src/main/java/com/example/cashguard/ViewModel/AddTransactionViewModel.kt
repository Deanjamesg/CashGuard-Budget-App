package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.Repository.TransactionRepository
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.data.Transaction
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionViewModel (application: Application) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository
    private val categoryRepository: CategoryRepository
    private val sessionManager: SessionManager

    private var userCategoryObjects: List<CategoryItem> = emptyList()

    private val _categoryNames = MutableLiveData<List<String>>()
    val categoryNames: LiveData<List<String>> = _categoryNames

    private val _transactionSaveStatus = MutableLiveData<Boolean?>()
    val transactionSaveStatus: LiveData<Boolean?> = _transactionSaveStatus

    var type: String = "Expense"

    init {
        val transactionDao = AppDatabase.getInstance(application).transactionDao()
        transactionRepository = TransactionRepository(transactionDao)
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        sessionManager = SessionManager(application)

        viewModelScope.launch {
            initializeAndLoadDefaultCategories()
        }
    }

    private suspend fun initializeAndLoadDefaultCategories() {

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            userCategoryObjects = categoryRepository.getCategorySpinner(userId)
            loadCategoriesForSpinner(type)
        } else {
            Log.e("AddTransactionVM", "No user ID found. Categories not loaded.")
            _categoryNames.postValue(listOf("No Categories Available"))
        }
    }

    // This method is called by the Fragment to update the categories based on Income/Expense type
    fun loadCategoriesForSpinner(filterType: String? = null) {
        val filteredCategoryNames = userCategoryObjects
            .filter { category ->
                filterType == null || category.type.equals(filterType, ignoreCase = true)
            }
            .map { it.name }
            .toMutableList()
        _categoryNames.postValue(filteredCategoryNames)
    }

    fun addTransaction(
        amount: Double,
        note: String?,
        categoryName: String,
        type: String,
        date: Date,
        photoUri: String? = null
    ) {
        val currentUserId = sessionManager.getUserId()

        // Create the Transaction object
        val newTransaction = Transaction(
            userId = currentUserId,
            date = date,
            amount = amount,
            note = note,
            photoUri = photoUri,
            type = type,
            categoryName = categoryName
        )

        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(newTransaction)
                Log.d("AddTransactionVM", "Transaction inserted successfully.")
                _transactionSaveStatus.postValue(true) // Notify success via LiveData
            } catch (e: Exception) {
                Log.e("AddTransactionVM", "Error inserting transaction", e)
                _transactionSaveStatus.postValue(false) // Notify failure via LiveData
            }
        }
    }

    fun onTransactionSaveStatusHandled() {
        _transactionSaveStatus.value = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "AddTransactionViewModel Cleared.")
    }
}