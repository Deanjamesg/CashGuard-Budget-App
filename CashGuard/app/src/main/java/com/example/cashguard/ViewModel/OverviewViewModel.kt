package com.example.cashguard.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashguard.Repository.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OverviewViewModel(
    private val transactionRepository: TransactionRepository,
    private val userId: Int // User ID obtained from factory
) : ViewModel() {

    // LiveData to hold the overview data
    private val _overviewData = MutableLiveData<OverviewData>()
    val overviewData: LiveData<OverviewData> get() = _overviewData

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // Function to trigger data loading
    fun loadOverviewDataForCurrentMonth() {
        Log.d("PieChartDebug", "OverviewViewModel: loadOverviewDataForCurrentMonth called for userId: $userId") // Log User ID

        viewModelScope.launch {
            try { // Add try-catch for safety during debugging
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_MONTH, 1); calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0); calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.time

                calendar.add(Calendar.MONTH, 1); calendar.add(Calendar.MILLISECOND, -1)
                val endDate = calendar.time

                Log.d("PieChartDebug", "OverviewViewModel: Date Range - Start: ${sdf.format(startDate)}, End: ${sdf.format(endDate)}") // Log Date Range

                // Fetch data from repository
                val income = transactionRepository.getTotalAmountByTypeAndDate(
                    userId, "Income", startDate, endDate
                ) ?: 0.0
                Log.d("PieChartDebug", "OverviewViewModel: Fetched Income = $income") // Log Fetched Income

                val expenses = transactionRepository.getTotalAmountByTypeAndDate(
                    userId, "Expense", startDate, endDate
                ) ?: 0.0
                Log.d("PieChartDebug", "OverviewViewModel: Fetched Expenses = $expenses") // Log Fetched Expenses

                // Calculate savings
                val calculatedSavings = income - expenses
                val savings = if (calculatedSavings > 0) calculatedSavings else 0.0
                Log.d("PieChartDebug", "OverviewViewModel: Calculated Savings = $savings (Raw: $calculatedSavings)") // Log Savings

                // Create data object
                val data = OverviewData(income, expenses, savings)
                Log.d("PieChartDebug", "OverviewViewModel: Posting OverviewData = $data") // Log Data Object

                // Post the results to LiveData
                _overviewData.postValue(data)

            } catch (e: Exception) {
                Log.e("PieChartDebug", "OverviewViewModel: Error loading overview data", e) // Log any errors
                _overviewData.postValue(OverviewData()) // Post empty data on error
            }
        }
    }

    init {
        Log.d("PieChartDebug", "OverviewViewModel: Initializing and loading data...") // Log Init
        loadOverviewDataForCurrentMonth()
    }

}