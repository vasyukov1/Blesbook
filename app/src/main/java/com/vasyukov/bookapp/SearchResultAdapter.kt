package com.vasyukov.bookapp

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vasyukov.bookapp.activities.BookDetailsActivity

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    private var searchResults: List<SearchResult> = emptyList()

    fun submitList(results: List<SearchResult>) {
        this.searchResults = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val result = searchResults[position]
        val book = result.book
        val quote = result.quote

        holder.tvBookTitle.text = book.title
        holder.tvBookAuthor.text = book.author
        holder.tvQuote.text = quote

        // Установка статуса книги
        when {
            book.isRead -> {
                holder.tvBookStatus.text = "Прочитано"
                holder.tvBookStatus.setTextColor(holder.itemView.context.getColor(R.color.green))
            }
            book.isWishlist -> {
                holder.tvBookStatus.text = "Wishlist"
                holder.tvBookStatus.setTextColor(holder.itemView.context.getColor(R.color.blue))
            }
            else -> {
                holder.tvBookStatus.text = "Не прочитано"
                holder.tvBookStatus.setTextColor(holder.itemView.context.getColor(R.color.gray))
            }
        }

        // Обработка нажатия на книгу
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookDetailsActivity::class.java).apply {
                putExtra("book", book)
            }
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(0, 0)
        }
    }

    // Получение количества результатов поиска
    override fun getItemCount(): Int = searchResults.size

    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBookTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val tvQuote: TextView = itemView.findViewById(R.id.tvQuote)
        val tvBookStatus: TextView = itemView.findViewById(R.id.tvBookStatus)
    }
}