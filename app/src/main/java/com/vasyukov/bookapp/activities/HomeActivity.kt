package com.vasyukov.bookapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vasyukov.bookapp.Book
import com.vasyukov.bookapp.NewsAdapter
import com.vasyukov.bookapp.NewsItem
import com.vasyukov.bookapp.R

class HomeActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvBooksRead: TextView
    private lateinit var tvBooksToRead: TextView
    private lateinit var btnEditProfile: ImageButton
    private lateinit var btnSearchByQuote: Button
    private lateinit var btnSearchByAuthorOrTitle: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var rvNewsFeed: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Инициализация элементов
        tvUserName = findViewById(R.id.tvUserName)
        tvBooksRead = findViewById(R.id.tvBooksRead)
        tvBooksToRead = findViewById(R.id.tvBooksToRead)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnSearchByQuote = findViewById(R.id.btnSearchByQuote)
        btnSearchByAuthorOrTitle = findViewById(R.id.btnSearchByAuthorOrTitle)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_home

        rvNewsFeed = findViewById(R.id.rvNewsFeed)
        rvNewsFeed.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(emptyList())
        rvNewsFeed.adapter = newsAdapter

        // Загрузка данных
        loadUserData()
        loadNewsFeed()

        // Обработка нажатия на кнопку "Редактировать"
        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // Обработка нажатия на кнопку "Поиска цитат"
        btnSearchByQuote.setOnClickListener {
            val intent = Intent(this, SearchByQuoteActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // Обработка нажатия на кнопку "Поиска книг"
        btnSearchByAuthorOrTitle.setOnClickListener {
            val intent = Intent(this, SearchByAuthorOrTitleActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
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

    // Возврат на экран
    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    // Загрузка информации о пользователе
    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "Имя")
        val lastName = sharedPreferences.getString("lastName", "Фамилия")

        // Загрузка списка книг
        val bookSharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val jsonBooks = bookSharedPreferences.getString("books", "[]")
        val typeBooks = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(jsonBooks, typeBooks)

        // Фильтр книг по статусу
        val readBooks = bookList.filter { it.isRead }
        val wishlistBooks = bookList.filter { it.isWishlist }

        // Отображение данных
        tvUserName.text = "$firstName $lastName"
        tvBooksRead.text = "Прочитано книг: ${readBooks.size}"
        tvBooksToRead.text = "Хочу прочитать: ${wishlistBooks.size}"
    }

    // Загрузка новостей
    private fun loadNewsFeed() {
        val sharedPreferences = getSharedPreferences("NewsFeed", MODE_PRIVATE)
        val json = sharedPreferences.getString("news", "[]")
        val type = object : TypeToken<List<NewsItem>>() {}.type
        val newsList = Gson().fromJson<List<NewsItem>>(json, type)

        newsAdapter = NewsAdapter(newsList)
        rvNewsFeed.adapter = newsAdapter
    }
}