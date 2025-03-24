package com.vasyukov.bookapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vasyukov.bookapp.Book
import com.vasyukov.bookapp.BookAdapter
import com.vasyukov.bookapp.R

class BookListActivity : AppCompatActivity() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bookAdapter: BookAdapter
    private var bookList: MutableList<Book> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        // Инициализация элементов
        rvBooks = findViewById(R.id.rvBooks)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_books

        // Настройка RecyclerView
        bookAdapter = BookAdapter(bookList, this)
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = bookAdapter

        // Загрузка данных
        loadBooks()

        // Toolbar
        bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Переход на главную страницу
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_books -> {
                    // Переход на экран "Мои книги"
                    startActivity(Intent(this, BookListActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_wishlist -> {
                    // Переход на экран "Хочу прочитать"
                    startActivity(Intent(this, WishlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_add -> {
                    // Переход на экран "Добавить книгу"
                    startActivity(Intent(this, AddBookActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    // Возврат на экран
    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    // Загрузка книг
    fun loadBooks() {
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val allBooks = Gson().fromJson<MutableList<Book>>(json, type)

        // Фильтр прочитанных книг
        val readBooks = allBooks.filter { it.isRead }
        bookAdapter.updateBooks(readBooks)
    }
}