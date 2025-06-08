package com.example.cashguard.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.Helper.SessionManager
import com.example.cashguard.Repository.CategoryRepository
import com.example.cashguard.data.ProgressBar
import kotlinx.coroutines.launch

class BudgetBalancesViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository: CategoryRepository
    private val sessionManager: SessionManager

    private val _progressBarData = MutableLiveData<List<ProgressBar>>()
    val progressBarData: LiveData<List<ProgressBar>> = _progressBarData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val categoryDao = AppDatabase.getInstance(application).categoryDao()
        categoryRepository = CategoryRepository(categoryDao)
        sessionManager = SessionManager(application)

        loadProgressBars()
    }

    private fun loadProgressBars() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val userId = sessionManager.getUserId()
            if (userId != "-1") {
                try {
                    val data = categoryRepository.getProgressBarData(userId)
                    _progressBarData.postValue(data)
                } catch (e: Exception) {
                    _progressBarData.postValue(emptyList())
                }
            } else {
                _progressBarData.postValue(emptyList())
            }
            _isLoading.postValue(false)
        }
    }
}