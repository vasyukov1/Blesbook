package com.vasyukov.bookapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vasyukov.bookapp.BookUtils
import com.vasyukov.bookapp.R
import com.vasyukov.bookapp.SearchResult
import com.vasyukov.bookapp.SearchResultAdapter

class SearchByQuoteActivity : AppCompatActivity() {

    private lateinit var etSearchQuote: EditText
    private lateinit var btnSearch: Button
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var tvNoResults: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var searchResultAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_by_quote)

        // Инициализация элементов
        etSearchQuote = findViewById(R.id.etSearchQuote)
        btnSearch = findViewById(R.id.btnSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)
        tvNoResults = findViewById(R.id.tvNoResults)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_home

        // Настройка RecyclerView
        searchResultAdapter = SearchResultAdapter()
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        rvSearchResults.adapter = searchResultAdapter

        // Обработка нажатия на кнопку поиска
        btnSearch.setOnClickListener {
            val query = etSearchQuote.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooksByQuote(query)
            } else {
                Toast.makeText(this, "Введите цитату для поиска", Toast.LENGTH_SHORT).show()
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

    // Поиск по цитате
    private fun searchBooksByQuote(query: String) {
        // Список всех книг
        val allBooks = BookUtils.getAllBooks(this)

        // Список результатов поиска
        val searchResults = mutableListOf<SearchResult>()

        // Поиск цитаты по запросу
        for (book in allBooks) {
            val matchingQuotes = book.quotes.filter { quote ->
                quote.contains(query, ignoreCase = true)
            }
            for (quote in matchingQuotes) {
                searchResults.add(SearchResult(book, quote))
            }
        }

        // Отображение результатов
        if (searchResults.isNotEmpty()) {
            searchResultAdapter.submitList(searchResults)
            tvNoResults.visibility = TextView.GONE
            rvSearchResults.visibility = RecyclerView.VISIBLE
        } else {
            searchResultAdapter.submitList(emptyList())
            tvNoResults.visibility = TextView.VISIBLE
            rvSearchResults.visibility = RecyclerView.GONE
        }
    }
}