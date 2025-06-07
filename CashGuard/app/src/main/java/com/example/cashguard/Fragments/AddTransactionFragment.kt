package com.example.cashguard.Fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cashguard.ViewModel.AddTransactionViewModel
import com.example.cashguard.databinding.FragmentAddTransactionBinding
import com.example.cashguard.R
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()

    private lateinit var categoryAutoCompleteTextView: AutoCompleteTextView
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>

    private var selectedCategoryName: String? = null
    private var selectedDate: Date? = null

    private lateinit var passedTransactionType: String

    private var selectedPhotoUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPhotoUri = uri
        if (uri != null) {
            Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        passedTransactionType = arguments?.getString("initialTransactionType") ?: "Expense"
        viewModel.type = passedTransactionType

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAutoCompleteTextView = binding.filledExposed

        // Setup the AutoCompleteTextView and its Adapter
        setupCategoryAutoComplete()

        // Observe changes from the ViewModel to update the Adapter
        observeViewModel()

        if (passedTransactionType == "Income") {
            binding.buttonTransactionType.check(R.id.button_income)
        } else {
            binding.buttonTransactionType.check(R.id.button_expense)
        }

        // ViewModel's init block handles initial loading.
        binding.buttonTransactionType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button_income -> viewModel.loadCategoriesForSpinner(filterType = "Income")
                    R.id.button_expense -> viewModel.loadCategoriesForSpinner(filterType = "Expense")
                }
                clearUIComponents()
            }
        }

        binding.btnDate.setOnClickListener {
            showDatePicker()
        }

        binding.buttonAddPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        setUpOnClickListeners()

        viewModel.transactionSaveStatus.observe(viewLifecycleOwner) { isSuccess ->
            isSuccess?.let { // Check if it's not null (it will be after onTransactionSaveStatusHandled)
                if (it) {
                    Toast.makeText(requireContext(), "Transaction added successfully!", Toast.LENGTH_LONG).show()
                    clearUIComponents()
                    // Optionally navigate back, clear fields, etc.
                    // findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to add transaction.", Toast.LENGTH_LONG).show()
                }
                viewModel.onTransactionSaveStatusHandled() // Reset the event
            }
        }
    }

    private fun setupCategoryAutoComplete() {

        autoCompleteAdapter = ArrayAdapter(
            requireContext(),
            R.layout.drop_down_item,
            mutableListOf<String>()
        )
        categoryAutoCompleteTextView.setAdapter(autoCompleteAdapter)
    }

    private fun observeViewModel() {
        viewModel.categoryNames.observe(viewLifecycleOwner) { categoryNameList ->
            autoCompleteAdapter.clear()
            if (categoryNameList != null) {
                autoCompleteAdapter.addAll(categoryNameList)
            }
        }
    }

    private fun setUpOnClickListeners() {

        categoryAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as? String

            if (selectedItem == null || selectedItem == "No Categories Available") {
                selectedCategoryName = null
                categoryAutoCompleteTextView.setText("", false)
                categoryAutoCompleteTextView.clearFocus()
            } else {
                selectedCategoryName = selectedItem
                Log.d(
                    "AddTransactionFragment",
                    "Selected category via AutoComplete: $selectedCategoryName"
                )
            }
        }

        // This listener handles cases where the dropdown is dismissed without an item click
        categoryAutoCompleteTextView.setOnDismissListener {

            val currentText = categoryAutoCompleteTextView.text.toString()
            if (currentText.isEmpty() || currentText == "") {
                selectedCategoryName = null
                categoryAutoCompleteTextView.setText("", false)
                categoryAutoCompleteTextView.clearFocus()
            }
        }

        binding.buttonAddTransaction.setOnClickListener {

            val transactionAmount = binding.editTextAmount.text.toString().toDoubleOrNull()
            val transactionNote = binding.editTextNote.text.toString()

            val category = selectedCategoryName

            val transactionType =
                if (binding.buttonTransactionType.checkedButtonId == R.id.button_income) "Income" else "Expense"
            val transactionDate =
                Calendar.getInstance().time // Replace with actual selected date from DatePicker

            if (category.isNullOrBlank() || category == "Select a Category" || category == "No Categories Available") {
                Toast.makeText(requireContext(), "Please select a category.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (transactionAmount == null || transactionAmount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            viewModel.addTransaction(
                amount = transactionAmount.toDouble(),
                note = transactionNote.takeIf { it.isNotBlank() },
                categoryName = category,
                type = transactionType,
                date = transactionDate,
                photoUri = selectedPhotoUri?.toString()
            )
        }
    }

    private fun clearUIComponents() {
        binding.editTextAmount.text.clear()
        binding.editTextNote.text.clear()
        categoryAutoCompleteTextView.setText("", false)
        selectedCategoryName = null
        categoryAutoCompleteTextView.clearFocus()
        selectedDate = null
        binding.btnDate.text = "Select Date"
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                binding.btnDate.text =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}