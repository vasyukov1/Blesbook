package com.vasyukov.bookapp

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vasyukov.bookapp.activities.BookDetailsActivity

class SearchBookAdapter : RecyclerView.Adapter<SearchBookAdapter.SearchBookViewHolder>() {

    private var bookList: List<Book> = emptyList()

    fun submitList(books: List<Book>) {
        this.bookList = books
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return SearchBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchBookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvBookTitle.text = book.title
        holder.tvBookAuthor.text = book.author

        // Установка статуса книге
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

    // Получение количества книг в результате
    override fun getItemCount(): Int = bookList.size

    class SearchBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBookTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val tvBookStatus: TextView = itemView.findViewById(R.id.tvBookStatus)
    }
}