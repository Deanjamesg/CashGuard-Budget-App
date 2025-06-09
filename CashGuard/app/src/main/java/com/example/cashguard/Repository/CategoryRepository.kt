package com.example.cashguard.Repository

import android.graphics.Color
import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.data.Category
import com.example.cashguard.data.CategoryItem
import com.example.cashguard.data.ProgressBar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CategoryRepository(private val categoryDao: CategoryDao) {

    private val categoryDatabaseReference = FirebaseDatabase.getInstance().getReference("categories")

    suspend fun insertCategory(category: Category) = categoryDao.insert(category)

    suspend fun getUserCategories(userId: String) = categoryDao.getCategoriesByUser(userId)

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
        categoryDatabaseReference.child(category.categoryId).setValue(category).await()
    }

    suspend fun getSpinnerCategories(userId: String): List<CategoryItem>? =
        categoryDao.getSpinnerCategories(userId)

    suspend fun getUserActiveCategories(userId: String): List<Category> =
         categoryDao.getActiveCategoriesByUser(userId)


    suspend fun deleteCategory(category: Category) {
        category.isActive = false
        categoryDao.update(category)
        categoryDatabaseReference.child(category.categoryId).setValue(category).await()
    }

    suspend fun getCategoryById(categoryId: String): Category {

        val category = categoryDao.getCategoryById(categoryId)

        return category
    }

    suspend fun insertCategories(categoriesToInsert: List<Category>) {
        val firebaseUpdates = mutableMapOf<String, Any>()
        val categoriesForRoom = mutableListOf<Category>()

        categoriesToInsert.forEach { category ->
            val firebaseKey = categoryDatabaseReference.push().key
                ?: throw Exception("Could not generate unique key from Firebase for a default category.")

            val completeCategory = category.copy(categoryId = firebaseKey)

            firebaseUpdates["/categories/$firebaseKey"] = completeCategory
            categoriesForRoom.add(completeCategory)
        }

        if (firebaseUpdates.isNotEmpty()) {
            FirebaseDatabase.getInstance().reference.updateChildren(firebaseUpdates).await()
            categoryDao.insertAll(categoriesForRoom)
        }
    }

    suspend fun getExpenseCategories(userId: String): List<Category> {
        return categoryDao.getExpenseCategoriesByUser(userId)
    }

    suspend fun getIncomeCategories(userId: String): List<Category> {
        return categoryDao.getIncomeCategoriesByUser(userId)
    }

    suspend fun addCategory(
        name: String,
        type: String,
        userId: String,
        budgetId: String,
        colorValue: Int = Color.BLACK
    ) {

        val firebaseKey = categoryDatabaseReference.push().key
            ?: throw Exception("Could not generate a unique key from Firebase for Category.")

        val newCategory = Category(
            categoryId = firebaseKey,
            userId = userId,
            budgetId = budgetId,
            name = name,
            type = type,
            minGoal = null,
            maxGoal = null,
            isActive = true,
            color = colorValue
        )

        // FIREBASE
        categoryDatabaseReference.child(firebaseKey).setValue(newCategory).await()

        // ROOM DB
        categoryDao.insert(newCategory)
    }


    suspend fun getProgressBarData(userId: String): List<ProgressBar>? {
        return categoryDao.getProgressBarData(userId)
    }

}