package com.vasyukov.bookapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.edit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vasyukov.bookapp.Book
import com.vasyukov.bookapp.BookUtils
import com.vasyukov.bookapp.NewsItem
import com.vasyukov.bookapp.R
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var btnExportData: Button
    private lateinit var btnImportData: Button
    private lateinit var btnSave: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    private val PICK_FILE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Инициализация элементов
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        btnExportData = findViewById(R.id.btnExportData)
        btnImportData = findViewById(R.id.btnImportData)
        btnSave = findViewById(R.id.btnSave)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_home

        // Загрузка данных о пользователе
        loadUserData()

        //  Обработка нажатия на кнопку "Экспорт"
        btnExportData.setOnClickListener {
            val isExported = BookUtils.exportBooksToJson(this)
            if (isExported) {
                Toast.makeText(this, "Данные успешно экспортированы", Toast.LENGTH_SHORT).show()
                val file = File(getExternalFilesDir(null), "books_export.json")
                if (file.exists()) {
                    shareFile(file)
                } else {
                    Toast.makeText(this, "Файл не найден. Сначала экспортируйте данные.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ошибка при экспорте данных", Toast.LENGTH_SHORT).show()
            }
        }

        //  Обработка нажатия на кнопку "Импорт"
        btnImportData.setOnClickListener {
            openFilePicker(this)
        }

        // Обработка нажатия на кнопку "Сохранить"
        btnSave.setOnClickListener {
            saveUserData()
            startActivity(Intent(this, HomeActivity::class.java))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val jsonString = readFileContent(uri)
                importBooksFromJson(jsonString)
            }
        }
    }

    // Отправка файла
    private fun shareFile(file: File) {
        val fileUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Поделиться файлом"))
    }

    // Открытие экрана с выбором файла
    fun openFilePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        activity.startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    // Чтение содержимого файла
    private fun readFileContent(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
            reader?.readText() ?: ""
        }
    }

    // Импорт всех книг из JSON-файла
    private fun importBooksFromJson(jsonString: String) {
        try {
            // Парсинг JSON
            val gson = Gson()
            val type = object : TypeToken<List<Book>>() {}.type
            val importedBooks = gson.fromJson<List<Book>>(jsonString, type)

            // Загрузка текущего списка книг
            val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
            val json = sharedPreferences.getString("books", "[]")
            val bookList = gson.fromJson<MutableList<Book>>(json, type)

            // Добавление импортированных книг без дублирования
            for (importedBook in importedBooks) {
                val existingBook = bookList.find { it.title == importedBook.title && it.author == importedBook.author }
                if (existingBook == null) {
                    bookList.add(importedBook)
                } else {
                    existingBook.isRead = importedBook.isRead
                    existingBook.isWishlist = importedBook.isWishlist
                }
            }

            // Сохраняем обновленный список
            val editor = sharedPreferences.edit()
            editor.putString("books", gson.toJson(bookList))
            editor.apply()

            loadUserData()
            Toast.makeText(this, "Книги успешно импортированы", Toast.LENGTH_SHORT).show()

            // Создание новости
            val newsItem = NewsItem(
                type = "books_imported",
                timestamp = System.currentTimeMillis()
            )

            // Сохранение новости
            val sharedPreferencesNews = this.getSharedPreferences("NewsFeed", MODE_PRIVATE)
            val jsonNews = sharedPreferencesNews.getString("news", "[]")
            val typeNews = object : TypeToken<MutableList<NewsItem>>() {}.type
            val newsList = Gson().fromJson<MutableList<NewsItem>>(jsonNews, typeNews)
            newsList.add(newsItem)

            sharedPreferencesNews.edit {
                putString("news", Gson().toJson(newsList))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при импорте данных", Toast.LENGTH_SHORT).show()
        }
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(0, 0)
    }

    // Загрузка информации о пользователе
    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        etFirstName.setText(sharedPreferences.getString("firstName", ""))
        etLastName.setText(sharedPreferences.getString("lastName", ""))
    }

    // Сохранение информации о пользователе
    private fun saveUserData() {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("firstName", etFirstName.text.toString())
        editor.putString("lastName", etLastName.text.toString())
        editor.apply()
    }
}