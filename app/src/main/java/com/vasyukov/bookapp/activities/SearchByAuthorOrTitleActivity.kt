package com.vasyukov.bookapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vasyukov.bookapp.BookUtils
import com.vasyukov.bookapp.R
import com.vasyukov.bookapp.SearchBookAdapter

class SearchByAuthorOrTitleActivity : AppCompatActivity() {

    private lateinit var etSearchQuery: EditText
    private lateinit var rgSearchFilter: RadioGroup
    private lateinit var btnSearch: Button
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var tvNoResults: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var searchBookAdapter: SearchBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_author_or_title)

        // Инициализация элементов
        etSearchQuery = findViewById(R.id.etSearchQuery)
        rgSearchFilter = findViewById(R.id.rgSearchFilter)
        btnSearch = findViewById(R.id.btnSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        tvNoResults = findViewById(R.id.tvNoResults)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_home

        // Настройка RecyclerView
        searchBookAdapter = SearchBookAdapter()
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = searchBookAdapter

        // Обработка нажатия на кнопку поиска
        btnSearch.setOnClickListener {
            val query = etSearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                Toast.makeText(this, "Введите запрос для поиска", Toast.LENGTH_SHORT).show()
            }
        }

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

    // Поиск книг
    private fun searchBooks(query: String) {
        // Список всех книг
        val allBooks = BookUtils.getAllBooks(this)

        // Определение фильтра
        val selectedFilterId = rgSearchFilter.checkedRadioButtonId
        val selectedFilter = when (selectedFilterId) {
            R.id.rbTitle -> "title"
            R.id.rbAuthor -> "author"
            else -> "all"
        }

        // ФИльтр книг
        val filteredBooks = allBooks.filter { book ->
            when (selectedFilter) {
                "title" -> book.title.contains(query, ignoreCase = true)
                "author" -> book.author.contains(query, ignoreCase = true)
                else -> book.title.contains(query, ignoreCase = true) || book.author.contains(query, ignoreCase = true)
            }
        }

        // Отображение результатов
        if (filteredBooks.isNotEmpty()) {
            searchBookAdapter.submitList(filteredBooks)
            tvNoResults.visibility = TextView.GONE
            rvSearchResults.visibility = RecyclerView.VISIBLE
        } else {
            searchBookAdapter.submitList(emptyList())
            tvNoResults.visibility = TextView.VISIBLE
            rvSearchResults.visibility = RecyclerView.GONE
        }
    }
}