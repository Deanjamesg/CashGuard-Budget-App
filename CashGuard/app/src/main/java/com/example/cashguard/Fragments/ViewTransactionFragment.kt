package com.example.cashguard.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cashguard.R
import com.example.cashguard.ViewModel.ViewTransactionViewModel
import com.example.cashguard.databinding.FragmentViewTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ViewTransactionFragment : Fragment() {

    private var _binding: FragmentViewTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ViewTransactionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getString("transactionId")

        if (transactionId != null) {
            viewModel.loadTransactionDetails(transactionId)
        } else {
            Toast.makeText(requireContext(), "Error: Transaction ID not found.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        viewModel.transaction.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                binding.transactionType.text = it.type
                binding.valueDate.text = dateFormat.format(it.date)
                binding.valueNote.text = it.note ?: "No note added."

                if (it.type.equals("Expense", ignoreCase = true)) {
                    binding.transactionType.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else {
                    binding.transactionType.setTextColor(ContextCompat.getColor(requireContext(), R.color.glow))
                }
            }
        }

        viewModel.categoryName.observe(viewLifecycleOwner) { categoryName ->
            binding.valueCategory.text = categoryName ?: "N/A"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}