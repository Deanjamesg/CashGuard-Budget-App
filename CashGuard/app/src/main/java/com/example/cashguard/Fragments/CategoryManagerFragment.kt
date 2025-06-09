package com.example.cashguard.Fragments

import androidx.appcompat.app.AlertDialog // Import the correct AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.Adapter.CategoryManagerAdapter
import com.example.cashguard.R
import com.example.cashguard.ViewModel.CategoryManagerViewModel
import com.example.cashguard.databinding.FragmentCategoryManagerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CategoryManagerFragment : Fragment() {

    private var _binding: FragmentCategoryManagerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryManagerViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryManagerAdapter
    private var selectedColor: Int = Color.parseColor("#484848")

    private var colorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSelectedColorView()
        setupRecyclerView()
        setupObservers()
        setupToggleGroupListener()
        setupClickListeners()

        if (binding.buttonCategoryType.checkedButtonId == R.id.button_income) {
            displayIncomeCategories()
        } else {
            displayExpenseCategories()
        }
    }

    private fun updateSelectedColorView() {
        val circleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(selectedColor)
            setStroke(4, Color.WHITE)
        }
        binding.viewSelectedColor.background = circleDrawable
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryManagerAdapter { category ->
            Log.d("CategoryFragment", "Delete category clicked: ${category.name}, Type: ${category.type}")
            viewModel.deleteCategory(category)
        }
        binding.categoriesContainer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }
    }

    private fun setupObservers() {
        viewModel.incomeCategories.observe(viewLifecycleOwner) { categories ->
            Log.d("CategoryFragment", "Observed ${categories.size} income categories.")
            if (binding.buttonCategoryType.checkedButtonId == R.id.button_income) {
                categoryAdapter.submitList(categories)
            }
        }

        viewModel.expenseCategories.observe(viewLifecycleOwner) { categories ->
            Log.d("CategoryFragment", "Observed ${categories.size} expense categories.")
            if (binding.buttonCategoryType.checkedButtonId == R.id.button_expense) {
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun setupToggleGroupListener() {
        binding.buttonCategoryType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_income -> displayIncomeCategories()
                    R.id.button_expense -> displayExpenseCategories()
                }
            }
        }
    }

    private fun displayIncomeCategories() {
        Log.d("CategoryFragment", "Displaying income categories")
        binding.editTextNewCategory.hint = "New Income Category"
        categoryAdapter.submitList(viewModel.incomeCategories.value ?: emptyList())
    }

    private fun displayExpenseCategories() {
        Log.d("CategoryFragment", "Displaying expense categories")
        binding.editTextNewCategory.hint = "New Expense Category"
        categoryAdapter.submitList(viewModel.expenseCategories.value ?: emptyList())
    }

    private fun setupClickListeners() {
        binding.viewSelectedColor.setOnClickListener {
            openColorPickerDialog()
        }

        binding.addCategory.setOnClickListener {
            val categoryName = binding.editTextNewCategory.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                val selectedTypeId = binding.buttonCategoryType.checkedButtonId
                val type = if (selectedTypeId == R.id.button_income) "Income" else "Expense"

                viewModel.addCategory(categoryName, type, selectedColor)
                binding.editTextNewCategory.text.clear()
                Log.d("CategoryFragment", "Add category clicked. Name: $categoryName, Type: $type, Color: $selectedColor")
            } else {
                Toast.makeText(context, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openColorPickerDialog() {

        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.red_bright),
            ContextCompat.getColor(requireContext(), R.color.violet),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.blue_light),
            ContextCompat.getColor(requireContext(), R.color.glow),
            ContextCompat.getColor(requireContext(), R.color.orange),
            ContextCompat.getColor(requireContext(), R.color.purple),
            ContextCompat.getColor(requireContext(), R.color.green_light),
            ContextCompat.getColor(requireContext(), R.color.dark_gray),
            ContextCompat.getColor(requireContext(), R.color.red_light),
            ContextCompat.getColor(requireContext(), R.color.ocean_blue),
            ContextCompat.getColor(requireContext(), R.color.blue),
        ).distinct()

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val colorGrid = dialogView.findViewById<GridLayout>(R.id.colorGrid)

        val itemSize = resources.getDimensionPixelSize(R.dimen.color_picker_item_size)
        val itemMargin = resources.getDimensionPixelSize(R.dimen.color_picker_item_margin)

        colors.forEach { colorInt ->
            val colorViewContainer = View(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = itemSize
                    height = itemSize
                    setMargins(itemMargin, itemMargin, itemMargin, itemMargin)
                }
                val circleDrawable = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(colorInt)
                    setStroke(4, Color.WHITE)
                }
                background = circleDrawable

                setOnClickListener {
                    selectedColor = colorInt
                    updateSelectedColorView()
                    colorDialog?.dismiss()
                }
            }
            colorGrid.addView(colorViewContainer)
        }

        colorDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomColorPickerDialogTheme)
            .setTitle("Select a Color for your Category")
            .setView(dialogView)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        colorDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        colorDialog?.dismiss()
        colorDialog = null
        binding.categoriesContainer.adapter = null
        _binding = null
    }
}