package com.example.cashguard.Fragments

import com.example.cashguard.ViewModel.CategoryViewModel
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.R // Use main project R

import com.example.cashguard.Adapter.BudgetCategoryAdapter
import com.example.cashguard.databinding.FragmentAddBudgetBinding
import com.example.cashguard.databinding.FragmentBudgetBalancesBinding
import com.example.cashguard.ViewModel.BudgetProgressBarView
import androidx.core.view.isNotEmpty
import com.example.cashguard.ViewModel.BudgetInfo


class BudgetBalancesFragment : Fragment() {

    private var _binding: FragmentBudgetBalancesBinding? = null
    private val binding get() = _binding!!

    private val budgetAdapter = BudgetCategoryAdapter()

    private var userId: Int = -1

    // --- Get ViewModels ---

    private lateinit var categoryViewModel: CategoryViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Retrieve userId from arguments
        // requireArguments() throws exception if arguments are null
        userId = requireArguments().getInt("USER_ID", -1) // Use the same key "USER_ID"
        if (userId == -1) {
            // Handle error if ID wasn't passed correctly
            Log.e("BudgetBalancesFragment", "ERROR: User ID not found in arguments!")
            // You might want to show an error message or close the fragment/activity
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBalancesBinding.inflate(inflater, container, false)

        // --- Initialize ONLY CategoryViewModel
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeViewModel()

        // --- Modify: Load data using the userId from arguments ---
        if (userId != -1) {
            categoryViewModel.loadBudgetInfo(userId)
        } else {
            // Already logged error in onCreate, maybe show placeholder UI
            Log.e("BudgetBalancesFragment", "Cannot load budget info due to invalid userId.")
        }

    }



    private fun setupRecyclerView() {
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.budgetRecyclerView.adapter = budgetAdapter // Use the adapter instance
    }


    private fun observeViewModel() {
        categoryViewModel.budgetInfoList.observe(viewLifecycleOwner) { budgetList ->
            Log.d("BudgetBalancesFragment", "Budget list updated: ${budgetList?.size ?: 0} items")

            if (budgetList.isNullOrEmpty()) {
                // List is empty: Show empty text, hide RecyclerView
                binding.emptyStateText.visibility = View.VISIBLE
                binding.budgetRecyclerView.visibility = View.GONE
            } else {
                // List has items: Hide empty text, show RecyclerView
                binding.emptyStateText.visibility = View.GONE
                binding.budgetRecyclerView.visibility = View.VISIBLE
                budgetAdapter.submitList(budgetList) // Update adapter
            }
        }
    }


    private fun setupFab() {
        binding.fabAddBudget.setOnClickListener {
            Log.d("BudgetBalancesFragment", "FAB clicked.")
            showAddBudgetDialog()
        }
    }

    private fun showAddBudgetDialog() {
        val dialog = AddBudgetDialogFragment().apply {
            setOnSaveClickListener { name, amount, color ->

                if (userId != -1) {
                    Log.d("AddBudgetDialog", "Save clicked - Name: $name, Amount: $amount, Color: $color, UserID: $userId")
                    categoryViewModel.addBudgetCategory(name, amount, color, userId)
                } else {
                    Log.e("AddBudgetDialog", "Cannot save budget, User ID is invalid.")
                }

            }
        }
        dialog.show(parentFragmentManager, "AddBudgetDialog")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class AddBudgetDialogFragment : DialogFragment() {

    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!
    private var selectedColor: Int = Color.BLACK // Default, will be updated

    private var onSaveClickListener: ((String, Double, Int) -> Unit)? = null

    fun setOnSaveClickListener(listener: (String, Double, Int) -> Unit) {
        onSaveClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedColor = ContextCompat.getColor(requireContext(), R.color.green)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d("CategoryInputFragment", "Inflated view for category:")
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupColorPicker()
        setupButtons()
    }

    private fun setupColorPicker() {
        // Use the color resources from the main project's R class
        val colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.lightBrown),
            ContextCompat.getColor(requireContext(), R.color.violet),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.lightBlue),
            ContextCompat.getColor(requireContext(), R.color.stroke),
            ContextCompat.getColor(requireContext(), R.color.glow),
            // Add any other standard colors you like using Color.parseColor or ContextCompat
        ).distinct() // Avoid duplicates

        binding.colorContainer.removeAllViews()
        // Use dimension resource from main project R class
        val swatchSize = try {
            resources.getDimensionPixelSize(R.dimen.color_picker_size)
        } catch (e: Exception) { 100 } // Fallback size
        val margin = (swatchSize * 0.1).toInt()

        var initialSelectionDone = false
        colors.forEach { colorInt ->
            val colorView = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(swatchSize, swatchSize).apply {
                    setMargins(margin, margin, margin, margin)
                }
                setBackgroundColor(colorInt)
                tag = colorInt // Store the color integer
                isClickable = true
                setOnClickListener { clickedView ->
                    selectedColor = clickedView.tag as Int
                    updateColorSelection(clickedView)
                }
                // Apply foreground tint for checkmark if needed (ensure ic_check_circle exists)
                // Example: foreground = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle)
                // foregroundTintList = ColorStateList.valueOf(Color.WHITE) // Example tint
            }
            binding.colorContainer.addView(colorView)

            if (colorInt == selectedColor && !initialSelectionDone) {
                updateColorSelection(colorView)
                initialSelectionDone = true
            }
        }
        // Fallback selection if default wasn't in the list
        if (!initialSelectionDone && binding.colorContainer.isNotEmpty()) {
            val firstChild = binding.colorContainer.getChildAt(0)
            selectedColor = firstChild.tag as? Int ?: ContextCompat.getColor(requireContext(), R.color.green)
            updateColorSelection(firstChild)
        }
    }


    private fun updateColorSelection(selectedView: View?) {
        val checkDrawable = try {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_circle)
        } catch (e: Exception) {
            Log.e("AddBudgetDialog", "ic_check_circle drawable not found!")
            null
        }
        checkDrawable?.setTint(Color.WHITE) // Set checkmark color

        for (i in 0 until binding.colorContainer.childCount) {
            val child = binding.colorContainer.getChildAt(i) as View
            child.foreground = null // Remove checkmark from others
            child.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
        }

        if (selectedView != null) {
            selectedView.foreground = checkDrawable // Add checkmark to selected
            selectedView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(100).start()
        }
    }


    private fun setupButtons() {
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.saveButton.setOnClickListener {
            binding.categoryNameLayout.error = null
            binding.budgetAmountLayout.error = null
            val name = binding.categoryNameEditText.text.toString().trim()
            val amountStr = binding.budgetAmountEditText.text.toString().trim()
            var isValid = true
            if (name.isBlank()) {
                binding.categoryNameLayout.error = "Enter a category name"
                isValid = false
            }
            var amount = 0.0
            if (amountStr.isBlank()) {
                binding.budgetAmountLayout.error = "Enter a budget amount"
                isValid = false
            } else {
                try {
                    amount = amountStr.toDouble()
                    if (amount <= 0) {
                        binding.budgetAmountLayout.error = "Amount must be positive"
                        isValid = false
                    }
                } catch (e: NumberFormatException) {
                    binding.budgetAmountLayout.error = "Invalid amount"
                    isValid = false
                }
            }
            if (isValid) {
                // Invoke listener -> Triggers ViewModel call in BudgetBalancesFragment
                onSaveClickListener?.invoke(name, amount, selectedColor)
                dismiss()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}