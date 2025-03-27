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
import androidx.core.content.edit

class EditBookActivity : AppCompatActivity() {

    private lateinit var etBookTitle: EditText
    private lateinit var etBookAuthor: EditText
    private lateinit var etBookCountry: EditText
    private lateinit var etBookYear: EditText
    private lateinit var btnSaveBook: Button
    private lateinit var rgBookList: RadioGroup
    private lateinit var bottomNavigationView: BottomNavigationView

    private var position: Int = -1
    private lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)

        // Инициализация элементов
        etBookTitle = findViewById(R.id.etBookTitle)
        etBookAuthor = findViewById(R.id.etBookAuthor)
        etBookCountry = findViewById(R.id.etBookCountry)
        etBookYear = findViewById(R.id.etBookYear)
        btnSaveBook = findViewById(R.id.btnSaveBook)
        rgBookList = findViewById(R.id.rgBookList)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_add

        // Получение данных о книге
        book = intent.getParcelableExtra<Book>("book")!!
        position = intent.getIntExtra("position", -1)

        // Заполнение полей данными книги
        etBookTitle.setText(book.title)
        etBookAuthor.setText(book.author)
        etBookCountry.setText(book.country)
        etBookYear.setText(book.year.toString())

        if (book.isWishlist) {
            rgBookList.check(R.id.rbWishlist)
        } else {
            rgBookList.check(R.id.rbRead)
        }

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

        // Обновление списка
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(json, type)

        val originalBook = bookList.find { it.title == book.title && it.author == book.author && it.country == book.country && it.year == book.year }
        if (originalBook != null) {
            bookList.remove(originalBook)
        }

        book.title = title
        book.author = author
        book.country = country
        book.year = year.toInt()
        book.isWishlist = isWishlist
        book.isRead = !isWishlist

        bookList.add(book)

        sharedPreferences.edit {
            putString("books", Gson().toJson(bookList))
        }
        Toast.makeText(this, "Книга изменена", Toast.LENGTH_SHORT).show()

        val newsItem = NewsItem(
            type = "book_edited",
            bookTitle = book.title,
            bookAuthor = book.author,
            timestamp = System.currentTimeMillis()
        )
        addNewsItem(newsItem)

        if (book.isWishlist) {
            startActivity(Intent(this, WishlistActivity::class.java))
        } else {
            startActivity(Intent(this, BookListActivity::class.java))
        }
        overridePendingTransition(0, 0)

    }

    // Добавление новости
    private fun addNewsItem(newsItem: NewsItem) {
        val sharedPreferences = getSharedPreferences("NewsFeed", MODE_PRIVATE)
        val json = sharedPreferences.getString("news", "[]")
        val type = object : TypeToken<MutableList<NewsItem>>() {}.type
        val newsList = Gson().fromJson<MutableList<NewsItem>>(json, type)
        newsList.add(newsItem)

        sharedPreferences.edit {
            putString("news", Gson().toJson(newsList))
        }
    }
}