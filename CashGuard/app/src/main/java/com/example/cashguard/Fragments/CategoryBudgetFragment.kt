package com.example.cashguard.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cashguard.Dao.CategoryDao
import com.example.cashguard.Database.AppDatabase
import com.example.cashguard.R
import com.example.cashguard.data.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryInputFragment : Fragment() {

    private lateinit var categoryDao: CategoryDao
    private lateinit var categoryContainer: LinearLayout
    private val categoryBudgets = mutableMapOf<String, Double>()
    private var userId: Int = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        Log.d("CategoryInputFragment", "Inflated view for category:")
        val view = inflater.inflate(R.layout.fragment_category_input, container, false)

        categoryContainer = view.findViewById(R.id.categoryContainer)

        // Initialize the DAO
        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao()

        // Fetch categories and display them
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) { categoryDao.getExpenseCategoryNames(userId) }
            if (categories.isNotEmpty()) {
                displayCategories(categories)
            } else {
                Log.e("CategoryInputFragment", "No categories found in the database.")
            }
        }

        return view
    }

    private fun displayCategories(categories: List<String>) {
        categoryContainer.removeAllViews()

        categories.forEach { category ->
            val categoryView = LayoutInflater.from(context).inflate(R.layout.fragment_category_input, categoryContainer, false)
            Log.d("CategoryInputFragment", "Inflated view for category: $category")
            val categoryNameTextView: TextView = categoryView.findViewById(R.id.categoryName)
            val budgetInputEditText: EditText = categoryView.findViewById(R.id.budgetInput)

            categoryNameTextView.text = category // Use the string directly

            budgetInputEditText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val amount = budgetInputEditText.text.toString().toDoubleOrNull() ?: 0.0
                    categoryBudgets[category] = amount // Use the string as the key
                }
            }

            categoryContainer.addView(categoryView)
        }
    }
}