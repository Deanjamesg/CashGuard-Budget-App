// Fragments/BarGraphFragment.kt
package com.example.cashguard.Fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.R
import com.example.cashguard.ViewModel.ChartDataPoint
import com.example.cashguard.ViewModel.BarGraphViewModel
import com.example.cashguard.databinding.FragmentBarGraphBinding
import org.eazegraph.lib.models.BarModel
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BarGraphFragment : Fragment() {

    private var _binding: FragmentBarGraphBinding? = null
    private val binding get() = _binding!!

    private lateinit var barGraphViewModel: BarGraphViewModel

    // Store the user's current selections
    private var selectedCategoryName: String? = null
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barGraphViewModel = ViewModelProvider(this).get(BarGraphViewModel::class.java)

        setupControls()
        observeViewModel()

        // Kick off the process by loading the categories for the spinner
        barGraphViewModel.loadCategoriesForSpinner()
    }

    private fun setupControls() {
        binding.buttonSelectFromDate.setOnClickListener {
            showDatePicker(isStartDatePicker = true)
        }
        binding.buttonSelectToDate.setOnClickListener {
            showDatePicker(isStartDatePicker = false)
        }
    }

    private fun observeViewModel() {
        // This observer populates the category dropdown spinner
        barGraphViewModel.categoryNamesForSpinner.observe(viewLifecycleOwner) { names ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            binding.categoryAutoCompleteTextView.setAdapter(adapter)

            // If we have categories, let's set a default view
            if (names.isNotEmpty()) {
                // Pre-select the first category
                selectedCategoryName = names[0]
                binding.categoryAutoCompleteTextView.setText(names[0], false)
                // And load a default date range
                loadDefaultDateRangeAndFetchData()
            }
        }

        // This observer waits for the final data point and draws the chart
        barGraphViewModel.chartDataPoint.observe(viewLifecycleOwner) { dataPoint ->
            if (dataPoint != null) {
                updateChartWithData(dataPoint)
            } else {
                binding.barChart.clearChart()
            }
        }
    }

    private fun loadDefaultDateRangeAndFetchData() {
        val calendar = Calendar.getInstance()
        endDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        startDate = calendar.time

        binding.textViewFromDate.text = formatDate(startDate!!)
        binding.textViewToDate.text = formatDate(endDate!!)

        // Now that defaults are set, request the chart data
        requestChartUpdate()
    }

    private fun showDatePicker(isStartDatePicker: Boolean) {
        val calendar = Calendar.getInstance()
        val dateToUse = if (isStartDatePicker) startDate else endDate
        dateToUse?.let { calendar.time = it }

        DatePickerDialog(requireContext(), R.style.DatePickerTheme, { _, year, month, day ->
            val selectedDate = Calendar.getInstance().apply { set(year, month, day) }.time
            if (isStartDatePicker) {
                startDate = selectedDate
                binding.textViewFromDate.text = formatDate(selectedDate)
            } else {
                endDate = selectedDate
                binding.textViewToDate.text = formatDate(selectedDate)
            }
            requestChartUpdate()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Central function to trigger a chart update
    private fun requestChartUpdate() {
        // Ensure all required selections have been made
        val category = selectedCategoryName
        val start = startDate
        val end = endDate

        if (category != null && start != null && end != null) {
            if (start.after(end)) {
                Toast.makeText(requireContext(), "Start date cannot be after end date.", Toast.LENGTH_SHORT).show()
                return
            }
            // All good, tell the ViewModel to fetch the data
            barGraphViewModel.loadChartDataForSelection(category, start, end)
        }
    }

    // Renamed from setupBarChart to be more specific
    private fun updateChartWithData(point: ChartDataPoint) {
        binding.barChart.clearChart()

        binding.categoryAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryName = barGraphViewModel.categoryNamesForSpinner.value?.get(position)
            requestChartUpdate()
        }

        val minColor = ContextCompat.getColor(requireContext(), R.color.yellow)
        val spentColor = ContextCompat.getColor(requireContext(), R.color.green)
        val maxColor = ContextCompat.getColor(requireContext(), R.color.red)

        binding.barChart.addBar(BarModel("Min Goal", point.minGoal, minColor))
        binding.barChart.addBar(BarModel("Spent", point.amountSpent, spentColor))
        binding.barChart.addBar(BarModel("Max Goal", point.maxGoal, maxColor))

        binding.barChart.startAnimation()
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}