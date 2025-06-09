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
import kotlinx.coroutines.launch
import java.util.Calendar

data class ChartDataPoint(
    val categoryName: String,
    val amountSpent: Float,
    val minGoal: Float,
    val maxGoal: Float,
    val color: Int
)

class BarGraphViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryRepository: CategoryRepository
    private val transactionRepository: TransactionRepository
    private val sessionManager: SessionManager

    private val _chartData = MutableLiveData<List<ChartDataPoint>>()
    val chartData: LiveData<List<ChartDataPoint>> = _chartData

    init {
        val db = AppDatabase.getInstance(application)
        categoryRepository = CategoryRepository(db.categoryDao())
        transactionRepository = TransactionRepository(db.transactionDao())
        sessionManager = SessionManager(application)
    }


    fun loadChartDataForPastMonth() {
        viewModelScope.launch {
            // Get the userId from the session
            val userId = sessionManager.getUserId()

            // Proceed only if the userId is valid
            if (userId != "-1") {
                val calendar = Calendar.getInstance()
                val endDate = calendar.time
                calendar.add(Calendar.MONTH, -1)
                val startDate = calendar.time

                Log.d("GraphViewModel", "Loading data for userId: $userId between $startDate and $endDate")

                val activeCategories = categoryRepository.getUserActiveCategories(userId)
                val expenseGoalCategories = activeCategories.filter { it.type == "Expense" && it.maxGoal != null && it.maxGoal > 0 }
                Log.d("GraphViewModel", "Found ${expenseGoalCategories.size} categories with goals.")

                val dataPoints = expenseGoalCategories.map { category ->
                    val spentAmount = transactionRepository.getSumExpensesByCategoryIdAndDateRange(
                        userId, category.categoryId, startDate, endDate
                    ) ?: 0.0

                    ChartDataPoint(
                        categoryName = category.name,
                        amountSpent = spentAmount.toFloat(),
                        minGoal = (category.minGoal ?: 0.0).toFloat(),
                        maxGoal = (category.maxGoal ?: 0.0).toFloat(),
                        color = category.color
                    )
                }
                _chartData.postValue(dataPoints)
                Log.d("GraphViewModel", "Posted ${dataPoints.size} data points to LiveData.")
            } else {
                Log.e("GraphViewModel", "Invalid userId from SessionManager. Cannot load chart data.")
                _chartData.postValue(emptyList()) // Post an empty list on error
            }
        }
    }
}