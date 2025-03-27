package com.vasyukov.bookapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

class NewsAdapter(
    private val context: Context,
    private val newsList: List<NewsItem>,
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNewsText: TextView = itemView.findViewById(R.id.tvNewsText)
        private val tvNewsDate: TextView = itemView.findViewById(R.id.tvNewsDate)
        private val viewUnreadIndicator: View = itemView.findViewById(R.id.viewUnreadIndicator)

        fun bind(newsItem: NewsItem) {
            val newsText = when (newsItem.type) {
                "book_added" -> "Добавлена книга: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "book_edited" -> "Книга изменена: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "book_read" -> "Книга прочитана: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "book_deleted" -> "Книга удалена: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "quote_added" -> "Добавлена цитата в книгу: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "photo_added" -> "Добавлено фото в книгу: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "books_imported" -> "Книги импортированы"
                else -> "Новое событие"
            }
            tvNewsText.text = newsText

            // Форматирование даты
            val dateFormat = SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault())
            val date = Date(newsItem.timestamp)
            tvNewsDate.text = dateFormat.format(date)

            // Индикатор прочтения новости
            viewUnreadIndicator.visibility = if (newsItem.isRead) View.GONE else View.VISIBLE

            // Отметка, что новость прочитана
            itemView.setOnClickListener {
                markAsRead(newsItem.id)
                notifyItemChanged(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        val sharedPreferences = context.getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
        val isRead = sharedPreferences.getBoolean(newsItem.id, false)
        newsItem.isRead = isRead
        holder.bind(newsItem)
    }

    // Получение количества новостей
    override fun getItemCount(): Int {
        return newsList.size
    }

    // Прочтение новости
    private fun markAsRead(newsId: String) {
        val sharedPreferences = context.getSharedPreferences("NewsPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit { putBoolean(newsId, true) }
    }
}