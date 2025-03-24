package com.vasyukov.bookapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vasyukov.bookapp.activities.BookDetailsActivity
import com.vasyukov.bookapp.activities.BookListActivity
import com.vasyukov.bookapp.activities.EditBookActivity
import com.vasyukov.bookapp.activities.WishlistActivity

class BookAdapter(
    private var bookList: MutableList<Book>,
    private val context: Context,
    private val isWishlist: Boolean = false
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBookNumber: TextView = itemView.findViewById(R.id.tvBookNumber)
        val tvBookTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
        val tvBookDetails: TextView = itemView.findViewById(R.id.tvBookDetails)
        val tvBookInfo: TextView = itemView.findViewById(R.id.tvBookInfo)
        val cbMarkAsRead: CheckBox = itemView.findViewById(R.id.cbMarkAsRead)

        init {
            // Обработчик нажатия на книгу
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val book = bookList[position]
                    openBookDetails(book)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.tvBookNumber.text = "${position + 1}."
        holder.tvBookTitle.text = book.title
        holder.tvBookAuthor.text = book.author
        holder.tvBookDetails.text = "${book.country}, ${book.year}"
        holder.tvBookInfo.text = "Цитаты: ${book.quotes.size}"

        // Показ галочки в вишлисте
        holder.cbMarkAsRead.visibility = if (isWishlist) View.VISIBLE else View.GONE

        // Обработка нажатия на кнопку "Прочитано"
        holder.cbMarkAsRead.setOnCheckedChangeListener(null)
        holder.cbMarkAsRead.isChecked = false
        holder.cbMarkAsRead.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                moveBookToReadList(book, position)
            }
        }

        // Обработка долгого нажатия на книгу
        holder.itemView.setOnLongClickListener {
            showEditDialog(book, position)
            true
        }
    }

    override fun getItemCount(): Int = bookList.size

    // Диалоговое окно для редактирования и удаления книги
    private fun showEditDialog(book: Book, position: Int) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Изменить книгу")
            .setMessage("Вы хотите изменить информацию о книге?")
            .setPositiveButton("Изменить") { _, _ ->
                val intent = Intent(context, EditBookActivity::class.java).apply {
                    putExtra("book", book)
                    putExtra("position", position)
                }
                context.startActivity(intent)
                (context as Activity).overridePendingTransition(0, 0)
            }
            .setNeutralButton("Удалить") { _, _ ->
                deleteBook(book, position)
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    // Перемещение книги из вишлиста в прочитанное
    private fun moveBookToReadList(book: Book, position: Int) {
        book.isWishlist = false
        book.isRead = true

        val sharedPreferences = context.getSharedPreferences("BookData", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(json, type)

        // Обновление книги
        val index = bookList.indexOfFirst { it.title == book.title && it.author == book.author }
        if (index != -1) {
            bookList[index] = book
        } else {
            bookList.add(book)
        }

        // Обновление списка
        val editor = sharedPreferences.edit()
        editor.putString("books", Gson().toJson(bookList))
        editor.apply()

        // Уведомление адаптера об изменении книги
        notifyItemChanged(position)

        // Создание новости
        val newsItem = NewsItem(
            type = "book_read",
            bookTitle = book.title,
            bookAuthor = book.author,
            timestamp = System.currentTimeMillis()
        )

        // Сохранение новости
        val sharedPreferencesNews = context.getSharedPreferences("NewsFeed", Context.MODE_PRIVATE)
        val jsonNews = sharedPreferencesNews.getString("news", "[]")
        val typeNews = object : TypeToken<MutableList<NewsItem>>() {}.type
        val newsList = Gson().fromJson<MutableList<NewsItem>>(jsonNews, typeNews)
        newsList.add(newsItem)

        val editorNews = sharedPreferencesNews.edit()
        editorNews.putString("news", Gson().toJson(newsList))
        editorNews.apply()

        // Уведомление активности об изменении книги
        if (context is WishlistActivity || context is BookListActivity) {
            (context as? WishlistActivity)?.loadWishlist()
            (context as? BookListActivity)?.loadBooks()
        }

        Toast.makeText(context, "Книга перемещена в список прочитанных", Toast.LENGTH_SHORT).show()    }

    // Открытие экрана "О книге"
    private fun openBookDetails(book: Book) {
        val intent = Intent(context, BookDetailsActivity::class.java).apply {
            putExtra("book", book)
        }
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }

    // Удаление книги
    private fun deleteBook(book: Book, position: Int) {
        bookList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, bookList.size)

        // Удаляем книгу из общего списка в SharedPreferences
        val sharedPreferences = context.getSharedPreferences("BookData", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val allBooks = Gson().fromJson<MutableList<Book>>(json, type)

        // Поиск книги в общем списке и удаление
        val index = allBooks.indexOfFirst { it.title == book.title && it.author == book.author }
        if (index != -1) {
            allBooks.removeAt(index)
        }

        // Обновление списка
        val editor = sharedPreferences.edit()
        editor.putString("books", Gson().toJson(allBooks))
        editor.apply()

        Toast.makeText(context, "Книга удалена", Toast.LENGTH_SHORT).show()
    }

    // Обновление списка книг
    fun updateBooks(newBooks: List<Book>) {
        bookList.clear()
        bookList.addAll(newBooks)
        notifyDataSetChanged()
    }
}