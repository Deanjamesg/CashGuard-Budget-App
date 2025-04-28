package com.example.cashguard.Model

import android.app.Application
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
    }

    suspend fun isEmailRegistered(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun validateUserCredentials(email: String, password: String): User? {
        return repository.validateUser(email, password)
    }
}