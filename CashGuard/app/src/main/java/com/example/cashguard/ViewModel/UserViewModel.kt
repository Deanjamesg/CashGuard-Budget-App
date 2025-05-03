package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Repository.UserRepository
import com.example.cashguard.data.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
    }

     fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)

         val userId = getUserIdByEmail(user.email)
         Log.d("UserViewModel", "USER ID: ${userId}")

         // Check if userId is not null before proceeding
         // Create default categories
         if (userId != null) {
             var categoryViewModel = CategoryViewModel(getApplication())
             categoryViewModel.createDefaultCategories(userId)
         }
    }

    suspend fun getUserByEmail(email: String): User? {
        return repository.getUserByEmail(email)
    }

    suspend fun getUserIdByEmail(email: String): Int? {
        return repository.getUserIdByEmail(email)
    }


    suspend fun isEmailRegistered(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun validateUserCredentials(email: String, password: String): User? {
        return repository.validateUser(email, password)
    }
}