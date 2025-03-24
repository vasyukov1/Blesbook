package com.vasyukov.bookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsAdapter(
    private val newsList: List<NewsItem>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNewsText: TextView = itemView.findViewById(R.id.tvNewsText)
        private val tvNewsDate: TextView = itemView.findViewById(R.id.tvNewsDate)

        fun bind(newsItem: NewsItem) {
            val newsText = when (newsItem.type) {
                "book_added" -> "Добавлена книга: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "book_edited" -> "Книга изменена: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "book_read" -> "Книга прочитана: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "quote_added" -> "Добавлена цитата в книгу: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                "photo_added" -> "Добавлено фото в книгу: ${newsItem.bookTitle} (${newsItem.bookAuthor})"
                else -> "Новое событие"
            }
            tvNewsText.text = newsText

            // Форматирование даты
            val dateFormat = SimpleDateFormat("HH:mm dd.MM.yy", Locale.getDefault())
            val date = Date(newsItem.timestamp)
            tvNewsDate.text = dateFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    // Получение количества новостей
    override fun getItemCount(): Int {
        return newsList.size
    }
}