package com.example.cashguard.Repository

import com.example.cashguard.Dao.BudgetDao
import com.example.cashguard.data.Budget
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.Date

class BudgetRepository(private val budgetDao: BudgetDao) {

    private val budgetDatabaseReference = FirebaseDatabase.getInstance().getReference("budgets")

    suspend fun insertBudget(budget: Budget) =
        budgetDao.insert(budget)

    suspend fun updateBudget(budget: Budget) =
        budgetDao.update(budget)

    suspend fun deleteBudget(budget: Budget) =
        budgetDao.delete(budget)

    suspend fun getBudgetsByUser(userId: String) =
        budgetDao.getAllBudgetsByUser(userId)

    suspend fun getUserBudgetByDateRange(userId: String, startDate: Date, endDate: Date) =
        budgetDao.getUserBudgetByDateRange(userId, startDate, endDate)

    suspend fun getBudgetTotalByBudgetId(userId: String, budgetId: String) =
        budgetDao.getBudgetTotalByBudgetId(userId, budgetId)

    suspend fun getCurrentBudget(userId: String): Budget {
        val today = Date()
        return budgetDao.getCurrentBudget(userId, today)
    }

    suspend fun createBudget(
        userId: String,
        startDate: Date,
        endDate: Date
    ): String {

        val firebaseKey = budgetDatabaseReference.push().key
            ?: throw Exception("Could not generate a unique key from Firebase for Budget.")

        val newBudget = Budget(
            budgetId = firebaseKey,
            userId = userId,
            budgetAmount = null,
            startDate = startDate,
            endDate = endDate
        )

        budgetDatabaseReference.child(firebaseKey).setValue(newBudget).await()

        insertBudget(newBudget)

        return firebaseKey
    }
}