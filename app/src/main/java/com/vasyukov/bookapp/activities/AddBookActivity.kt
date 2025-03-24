package com.vasyukov.bookapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vasyukov.bookapp.Book
import com.vasyukov.bookapp.NewsItem
import com.vasyukov.bookapp.R

class AddBookActivity : AppCompatActivity() {

    private lateinit var etBookTitle: EditText
    private lateinit var etBookAuthor: EditText
    private lateinit var etBookCountry: EditText
    private lateinit var etBookYear: EditText
    private lateinit var rgBookList: RadioGroup
    private lateinit var btnSaveBook: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        // Инициализация элементов
        etBookTitle = findViewById(R.id.etBookTitle)
        etBookAuthor = findViewById(R.id.etBookAuthor)
        etBookCountry = findViewById(R.id.etBookCountry)
        etBookYear = findViewById(R.id.etBookYear)
        rgBookList = findViewById(R.id.rgBookStatus)
        btnSaveBook = findViewById(R.id.btnSaveBook)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_add

        // Обработка нажатия на кнопку "Сохранить"
        btnSaveBook.setOnClickListener {
            saveBook()
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

    // Сохранение книги
    private fun saveBook() {
        // Элементы книги
        val title = etBookTitle.text.toString()
        val author = etBookAuthor.text.toString()
        val country = etBookCountry.text.toString()
        val year = etBookYear.text.toString()

        if (title.isEmpty() || author.isEmpty() || country.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButtonId = rgBookList.checkedRadioButtonId
        val isWishlist = selectedRadioButtonId == R.id.rbWishlist

        // Новая книга
        val book = Book(title, author, country, year.toInt(), mutableListOf(), !isWishlist, isWishlist)

        // Сохранение книги в общий список
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(json, type)
        bookList.add(book)

        val editor = sharedPreferences.edit()
        editor.putString("books", Gson().toJson(bookList))
        editor.apply()

        // Создание новости
        val newsItem = NewsItem(
            type = "book_added",
            bookTitle = book.title,
            bookAuthor = book.author,
            timestamp = System.currentTimeMillis()
        )
        addNewsItem(newsItem)

        // Очищение полей ввода
        etBookTitle.text.clear()
        etBookAuthor.text.clear()
        etBookCountry.text.clear()
        etBookYear.text.clear()
        rgBookList.clearCheck()

        Toast.makeText(this, "Книга добавлена", Toast.LENGTH_SHORT).show()
        overridePendingTransition(0, 0)
    }

    // Добавление новости
    private fun addNewsItem(newsItem: NewsItem) {
        val sharedPreferences = getSharedPreferences("NewsFeed", MODE_PRIVATE)
        val json = sharedPreferences.getString("news", "[]")
        val type = object : TypeToken<MutableList<NewsItem>>() {}.type
        val newsList = Gson().fromJson<MutableList<NewsItem>>(json, type)
        newsList.add(newsItem)

        val editor = sharedPreferences.edit()
        editor.putString("news", Gson().toJson(newsList))
        editor.apply()
    }
}