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
import java.util.Date

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

    // Holds the single data point for the currently selected category and date range
    private val _chartDataPoint = MutableLiveData<ChartDataPoint?>()
    val chartDataPoint: LiveData<ChartDataPoint?> = _chartDataPoint

    // Holds the list of category names for the spinner
    private val _categoryNamesForSpinner = MutableLiveData<List<String>>()
    val categoryNamesForSpinner: LiveData<List<String>> = _categoryNamesForSpinner

    // Stores all categories with goals so we don't have to query them repeatedly
    private var categoriesWithGoals: List<com.example.cashguard.data.Category> = emptyList()

    init {
        val db = AppDatabase.getInstance(application)
        categoryRepository = CategoryRepository(db.categoryDao())
        transactionRepository = TransactionRepository(db.transactionDao())
        sessionManager = SessionManager(application)
    }

    // Call this once to populate the spinner
    fun loadCategoriesForSpinner() {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != "-1") {
                // Fetch and store the categories with goals
                categoriesWithGoals = categoryRepository.getUserActiveCategories(userId)
                    .filter { it.type == "Expense" && it.maxGoal != null && it.maxGoal > 0 }

                // Post just the names to the spinner's LiveData
                _categoryNamesForSpinner.postValue(categoriesWithGoals.map { it.name })
            }
        }
    }

    // Call this whenever the category or date range changes
    fun loadChartDataForSelection(categoryName: String, startDate: Date, endDate: Date) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            // Find the full category object from our stored list
            val selectedCategory = categoriesWithGoals.find { it.name == categoryName }

            if (userId != "-1" && selectedCategory != null) {
                // Get the spending for just this category in the selected date range
                val spentAmount = transactionRepository.getSumExpensesByCategoryIdAndDateRange(
                    userId, selectedCategory.categoryId, startDate, endDate
                ) ?: 0.0

                // Create a single ChartDataPoint and post it
                val dataPoint = ChartDataPoint(
                    categoryName = selectedCategory.name,
                    amountSpent = spentAmount.toFloat(),
                    minGoal = (selectedCategory.minGoal ?: 0.0).toFloat(),
                    maxGoal = (selectedCategory.maxGoal ?: 0.0).toFloat(),
                    color = selectedCategory.color
                )
                _chartDataPoint.postValue(dataPoint)
            } else {
                // If selection is invalid, post null to clear the chart
                _chartDataPoint.postValue(null)
            }
        }
    }
}