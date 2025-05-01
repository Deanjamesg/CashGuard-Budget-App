package com.example.cashguard.Repository

import com.example.cashguard.Dao.BudgetDao
import com.example.cashguard.data.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {
    suspend fun insertBudget(budget: Budget) = budgetDao.insert(budget)
    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
    suspend fun deleteBudget(budget: Budget) = budgetDao.delete(budget)
    suspend fun getBudgets(userId: Int) = budgetDao.getBudgetsByUser(userId)
    suspend fun getBudgetByMonth(userId: Int, month: String) = budgetDao.getBudgetByMonth(userId, month)
}