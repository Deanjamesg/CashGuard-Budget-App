// Fragments/GraphFragment.kt
package com.example.cashguard.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cashguard.R
import com.example.cashguard.ViewModel.ChartDataPoint
import com.example.cashguard.ViewModel.BarGraphViewModel
import com.example.cashguard.databinding.FragmentBarGraphBinding
import org.eazegraph.lib.models.BarModel

class BarGraphFragment : Fragment() {

    // Properties for the view binding and our ViewModel.
    private var _binding: FragmentBarGraphBinding? = null
    private val binding get() = _binding!!
    private lateinit var graphViewModel: BarGraphViewModel

    // Standard boilerplate to inflate the layout using view binding.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    // This runs after the view has been created. Good place for setup code.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a reference to our GraphViewModel.
        // It's scoped to this fragment's lifecycle.
        graphViewModel = ViewModelProvider(this).get(BarGraphViewModel::class.java)

        // Set up the observer to listen for data changes.
        observeViewModel()

        // Kick off the initial data load for the chart.
        // The ViewModel already knows how to get the userId from the SessionManager.
        graphViewModel.loadChartDataForPastMonth()
    }

    // Sets up the observer on the ViewModel's LiveData.
    private fun observeViewModel() {
        graphViewModel.chartData.observe(viewLifecycleOwner) { dataPoints ->
            // This block runs whenever the data is fetched or updated.
            if (dataPoints.isNotEmpty()) {
                binding.barChart.visibility = View.VISIBLE
                setupBarChart(dataPoints)
            } else {
                // If there's no data, clear the chart and hide it.
                // Could also show a "No data to display" message here.
                binding.barChart.clearChart()
                binding.barChart.visibility = View.INVISIBLE
            }
        }
    }

    // Main function to configure and draw the chart once we have data.
    private fun setupBarChart(dataPoints: List<ChartDataPoint>) {
        // Always clear the chart before adding new data to prevent duplicates.
        binding.barChart.clearChart()

        // Loop through each data point from the ViewModel to build a bar for it.
        for (point in dataPoints) {
            // The main value for the bar is how much has been spent.
            // The legend is the text that shows up when you tap the bar.
            val spentBar = BarModel(
                point.categoryName,
                point.amountSpent,
                Color.parseColor("#56B7F1") // A default color, we'll override it below.
            )

            // Here's the core visual logic to meet the POE requirements.
            // We change the bar's color based on how the spending compares to the goals.
            val barColor = when {
                point.amountSpent > point.maxGoal -> ContextCompat.getColor(requireContext(), R.color.red)    // Over budget = Red
                point.amountSpent >= point.minGoal -> ContextCompat.getColor(requireContext(), R.color.green) // In the safe zone = Green
                else -> ContextCompat.getColor(requireContext(), R.color.yellow) // Under budget = Yellow
            }
            spentBar.color = barColor

            // Set the pop-up legend text to show the full details on tap.
            spentBar.legendLabel = "Spent: ${point.amountSpent.toInt()}, Goal: ${point.minGoal.toInt()}-${point.maxGoal.toInt()}"

            // Add the fully configured bar to the chart.
            binding.barChart.addBar(spentBar)
        }

        // Kick off the cool "grow" animation for the bars.
        binding.barChart.startAnimation()
    }

    // Clean up the binding reference when the view is destroyed to avoid memory leaks.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}