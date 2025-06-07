package com.example.cashguard.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast // Import Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cashguard.Adapter.SearchTransactionItemAdapter
import com.example.cashguard.R
import com.example.cashguard.ViewModel.SearchTransactionsViewModel
import com.example.cashguard.databinding.FragmentSearchTransactionsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchTransactionsFragment : Fragment() {

    private var _binding: FragmentSearchTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchTransactionsViewModel by viewModels()
    private lateinit var transactionAdapter: SearchTransactionItemAdapter

    private lateinit var typeAutoCompleteTextView: AutoCompleteTextView
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>

    private var selectedFromDate: Date? = null
    private var selectedToDate: Date? = null
    private var selectedType: String? = null

    private val transactionTypes: List<String> = listOf("All", "Income", "Expense")
    private val uiDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeAutoCompleteTextView = binding.typeExposed

        setupRecyclerView()
        setupTypeAutoComplete()
        setUpOnClickListeners()
        setupDatePickers()
        setupObservers()

        updateDateRangeText()


        binding.btnSearch.setOnClickListener {
            if (selectedFromDate == null || selectedToDate == null) {
                Toast.makeText(context, "Please select both From and To dates.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedFromDate!! > selectedToDate!!) {
                Toast.makeText(context, "From date cannot be after To date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.searchTransactionsOnClick(selectedType, selectedFromDate, selectedToDate)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = SearchTransactionItemAdapter()
        binding.transactionsContainerList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }

    private fun setupObservers() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            Log.d("SearchFragment", "Transactions updated: ${transactions?.size ?: 0}")
            if (transactions.isNullOrEmpty() && (selectedFromDate != null && selectedToDate != null)) {
                Toast.makeText(context, "No transactions found for the selected criteria.", Toast.LENGTH_SHORT).show()
            }
            updateDateRangeText()
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpOnClickListeners() {
        typeAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            selectedType = parent.getItemAtPosition(position) as? String
        }

        typeAutoCompleteTextView.setOnDismissListener {
            val currentText = typeAutoCompleteTextView.text.toString()
            if (currentText.isEmpty() || currentText == "") {
                selectedType = null
                typeAutoCompleteTextView.setText("", false)
                typeAutoCompleteTextView.clearFocus()
            }

        }
    }

    private fun setupTypeAutoComplete() {
        autoCompleteAdapter = ArrayAdapter(
            requireContext(),
            R.layout.drop_down_item,
            transactionTypes
        )
        typeAutoCompleteTextView.setAdapter(autoCompleteAdapter)
    }

    private fun setupDatePickers() {
        binding.btnFromDate.setOnClickListener { showDatePicker(isFromDate = true) }
        binding.btnToDate.setOnClickListener { showDatePicker(isFromDate = false) }
    }

    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, year, month, day ->
                calendar.apply {
                    set(year, month, day)
                    if (isFromDate) {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        selectedFromDate = time
                        binding.btnFromDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(time)
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        selectedToDate = time
                        binding.btnToDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(time)
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateRangeText() {
        if (selectedFromDate != null && selectedToDate != null) {
            val fromStr = uiDateFormat.format(selectedFromDate!!)
            val toStr = uiDateFormat.format(selectedToDate!!)
            binding.tvDateRange.text = "$fromStr – $toStr"
        }
        else {
            binding.tvDateRange.text = ""
        }
    }

    private fun clearUIComponents() {
        typeAutoCompleteTextView.setText("", false)
        selectedFromDate = null
        selectedToDate = null
        binding.tvDateRange.text = ""

    }

    override fun onDestroyView() {
//        viewModel.clearTransactions()
        super.onDestroyView()
        _binding = null
    }
}