package com.example.cashguard.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.R
import com.example.cashguard.data.SearchTransactionItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class SearchTransactionItemAdapter(
    private val onItemClicked: (SearchTransactionItem) -> Unit
) : ListAdapter<SearchTransactionItem, SearchTransactionItemAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: TextView = itemView.findViewById(R.id.tvItemCategory)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvItemDate)
        private val amountTextView: TextView = itemView.findViewById(R.id.tvItemAmount)

        fun bind(transaction: SearchTransactionItem) {
            categoryTextView.text = transaction.categoryName
            dateTextView.text = dateFormat.format(transaction.date)
            amountTextView.text = currencyFormatter.format(transaction.amount)

            val amountColor = if (transaction.type.equals("Income", ignoreCase = true)) {
                ContextCompat.getColor(itemView.context, R.color.glow)
            } else {
                ContextCompat.getColor(itemView.context, R.color.red)
            }
            amountTextView.setTextColor(amountColor)

            itemView.setOnClickListener {
                onItemClicked(transaction)
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<SearchTransactionItem>() {
        override fun areItemsTheSame(oldItem: SearchTransactionItem, newItem: SearchTransactionItem): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: SearchTransactionItem, newItem: SearchTransactionItem): Boolean {
            return oldItem == newItem
        }
    }
}