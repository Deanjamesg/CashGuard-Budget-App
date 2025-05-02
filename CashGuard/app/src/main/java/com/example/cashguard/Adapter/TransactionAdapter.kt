package com.example.cashguard.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ItemTransactionActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // Inflate the layout for each item
        val binding = ItemTransactionActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    // Bind the data to the views
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Get the transaction at the current position
        val transaction = transactions[position]
        with(holder.binding) {
            // Set the transaction details to the views
            tvAmount.text = "R${transaction.amount}"
            tvCategory.text = transaction.categoryName
            tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(transaction.date)
        }
    }

    override fun getItemCount() = transactions.size

    // Update the list of transactions and notify the adapter
    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}