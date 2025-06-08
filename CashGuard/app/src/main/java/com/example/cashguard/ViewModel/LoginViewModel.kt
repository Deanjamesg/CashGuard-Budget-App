package com.example.cashguard.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val sessionManager: SessionManager

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    private val _signedIn = MutableLiveData<Boolean?>()
    val signedIn: LiveData<Boolean?> = _signedIn

    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        userRepository = UserRepository(userDao)
        sessionManager = SessionManager(application)

        isUserSignedIn()
    }

    fun onLoginClicked(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _toastMessage.value = "Please enter in all details."
            return
        }

        viewModelScope.launch {
            try {
                val user = userRepository.validateUser(email, password)
                if (user != null) {
                    sessionManager.saveUserSession(userId = user.userId)
                    _toastMessage.value = "Welcome back, ${user.firstName}!"
                    isUserSignedIn()
                }
                else {
                    _toastMessage.value = "Invalid email or password."
                }

            } catch (e: Exception) {
                Log.d("Login", "EXCEPTION ${e.message}")
                return@launch
            }
        }
    }

    fun onToastMessageShown() {
        _toastMessage.value = null
    }

    private fun isUserSignedIn() {
        val userId = sessionManager.getUserId()
        _signedIn.value = userId != "-1"
        return
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModel", "LoginViewModel Cleared.")
    }
}

