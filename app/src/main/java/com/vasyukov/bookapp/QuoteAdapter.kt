package com.vasyukov.bookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuoteAdapter(
    private val quoteList: List<String>,
    private val onQuoteLongClick: (Int) -> Unit
) : RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder>() {

    inner class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuoteNumber: TextView = itemView.findViewById(R.id.tvQuoteNumber)
        val tvQuote: TextView = itemView.findViewById(R.id.tvQuote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val quote = quoteList[position]
        holder.tvQuoteNumber.text = "${position + 1}."
        holder.tvQuote.text = quote

        // Обработка долгого нажатия на цитату
        holder.itemView.setOnLongClickListener {
            onQuoteLongClick(position)
            true
        }
    }

    // Получение количества цитат
    override fun getItemCount(): Int {
        return quoteList.size
    }
}