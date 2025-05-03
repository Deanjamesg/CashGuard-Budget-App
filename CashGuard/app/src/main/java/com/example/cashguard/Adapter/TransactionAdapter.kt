package com.example.cashguard.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.cashguard.R
import com.example.cashguard.data.Transaction
import com.example.cashguard.databinding.ItemTransactionActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        with(holder.binding) {
            tvAmount.text = "R${transaction.amount}"
            tvCategory.text = transaction.categoryName
            tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(transaction.date)

            if (transaction.photoUri.isNullOrEmpty()) {
                ivAttachment.visibility = View.GONE
            } else {
                ivAttachment.setImageResource(R.drawable.attach_outline)
                ivAttachment.visibility = View.VISIBLE

                ivAttachment.setOnClickListener {
                    val uri = transaction.photoUri.toUri()
                    val context = holder.itemView.context
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = uri
                        type = "image/*"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK // Open in a new task
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}