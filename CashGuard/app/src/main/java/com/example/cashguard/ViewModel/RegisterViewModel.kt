package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.R
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.Repository.UserRepository
import com.example.cashguard.data.Category
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val categoryRepository: CategoryRepository

    private val sessionManager: SessionManager

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _registerState = MutableLiveData<Boolean?>()
    val registerState: LiveData<Boolean?> = _registerState

    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        userRepository = UserRepository(userDao)

        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)

        sessionManager = SessionManager(application)
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Triple("Food & Drink", "Expense", R.color.red),
            Triple("Transport", "Expense", R.color.blue_light),
            Triple("Household", "Expense", R.color.yellow),
            Triple("Entertainment", "Expense", R.color.violet),
            Triple("Subscription", "Expense", R.color.green),
            Triple("Salary", "Income", R.color.stroke)
        )
    }

    fun onRegisterClicked(firstName: String, lastName: String, email: String, password: String, confirmPassword: String) {

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _toastMessage.value = "Please enter in all details."
            return
        }
        if (password.length < 4) {
            _toastMessage.value = "Password must be at least 4 characters long."
            return
        }
        if (password != confirmPassword) {
            _toastMessage.value = "Passwords do not match."
            return
        }

        viewModelScope.launch {
            if (userRepository.getUserByEmail(email) != null) {
                _toastMessage.value = "Registration failed, this email already exists."
                return@launch
            }
            try {
                val userId = userRepository.insertUserAndGetId(firstName, lastName, email, password)
                _toastMessage.value = "Registration successful!"
                _registerState.value = true
                createDefaultCategories(userId)
            } catch (e: Exception) {
                _toastMessage.value = "Registration failed, please try again later."
                return@launch
            }
        }
    }

    private suspend fun createDefaultCategories(userId: Int) {
        val context = getApplication<Application>().applicationContext
        val defaultCategories = DEFAULT_CATEGORIES.map { (name, type, colorResId) ->
            Category(
                userId = userId,
                name = name,
                type = type,
                budgetAmount = null,
                color = ContextCompat.getColor(context, colorResId)
            )
        }
        Log.d("RegisterViewModel", "Creating ${defaultCategories.size} default categories.")
        categoryRepository.insertCategories(defaultCategories)
    }

    fun onToastMessageShown() {
        _toastMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "RegisterViewModel Cleared.")
    }
}

