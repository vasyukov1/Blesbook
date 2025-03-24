package com.vasyukov.bookapp.activities

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vasyukov.bookapp.Book
import com.vasyukov.bookapp.NewsItem
import com.vasyukov.bookapp.PhotoAdapter
import com.vasyukov.bookapp.QuoteAdapter
import com.vasyukov.bookapp.R
import java.io.File
import java.io.OutputStream

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookAuthor: TextView
    private lateinit var tvBookDetails: TextView
    private lateinit var rvQuotes: RecyclerView
    private lateinit var rvPhotos: RecyclerView
    private lateinit var btnAddQuote: Button
    private lateinit var btnAddPhoto: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        private const val REQUEST_CAMERA = 1002
    }

    private var quoteList: MutableList<String> = mutableListOf()
    private var photoList: MutableList<String> = mutableListOf()
    private var currentPhotoPath: String? = null

    private lateinit var book: Book
    private lateinit var quoteAdapter: QuoteAdapter
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        // Доступ к камере
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }

        // Инициализация элементов
        tvBookTitle = findViewById(R.id.tvBookTitle)
        tvBookAuthor = findViewById(R.id.tvBookAuthor)
        tvBookDetails = findViewById(R.id.tvBookDetails)
        rvQuotes = findViewById(R.id.rvQuotes)
        rvPhotos = findViewById(R.id.rvPhotos)
        btnAddQuote = findViewById(R.id.btnAddQuote)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.selectedItemId = R.id.navigation_add

        // Получение данных о книге
        book = intent.getParcelableExtra<Book>("book")!!

        // Отображение информации о книге
        tvBookTitle.text = book.title
        tvBookAuthor.text = book.author
        tvBookDetails.text = "${book.country}, ${book.year}"

        // Инициализация списка цитат
        quoteList.clear()
        quoteList.addAll(book.quotes)

        // Настройка RecyclerView для цитат
        quoteAdapter = QuoteAdapter(quoteList) { position ->
            showEditQuoteDialog(position)
        }
        rvQuotes.layoutManager = LinearLayoutManager(this)
        rvQuotes.adapter = quoteAdapter

        // Настройка RecyclerView для фото
        photoAdapter = PhotoAdapter(photoList) { position ->
            showDeletePhotoDialog(position)
        }
        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPhotos.adapter = photoAdapter

        // Загрузка данных
        loadQuotes()
        loadPhotos()

        // Обработка нажатия на кнопку "Добавить цитату"
        btnAddQuote.setOnClickListener {
            showAddQuoteDialog()
        }

        // Обработка нажатия на кнопку "Добавить фото"
        btnAddPhoto.setOnClickListener {
            openGalleryOrCamera()
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

    // Запрос разрешения камеры
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    // Выбор открытия галереи или камеры
    private fun openGalleryOrCamera() {
        val options = arrayOf("Галерея", "Камера")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник фото")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> galleryLauncher.launch("image/*")
                    1 -> openCamera()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Открытие камеры
    private fun openCamera() {
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath
        val photoURI = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoURI)
    }

    // Создание фото
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "photo_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    // Диалоговое окно для удаления фото
    private fun showDeletePhotoDialog(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Удалить фотографию?")
            .setMessage("Вы уверены, что хотите удалить эту фотографию?")
            .setPositiveButton("Удалить") { _, _ ->
                deletePhoto(position)
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Удаление фото
    private fun deletePhoto(position: Int) {
        photoList.removeAt(position)
        savePhotos()
        photoAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Фотография удалена", Toast.LENGTH_SHORT).show()
    }

    // Диалоговое окно для добавления цитаты
    private fun showAddQuoteDialog() {
        val input = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Добавить цитату")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val quote = input.text.toString()
                if (quote.isNotEmpty()) {
                    quoteList.add(quote)
                    book.quotes = quoteList
                    saveQuotes()
                    quoteAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
    }

    // Сохранение цитат
    private fun saveQuotes() {
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(json, type)

        // Поиск книги по названию и автору
        val index = bookList.indexOfFirst { it.title == book.title && it.author == book.author }

        if (index != -1) {
            bookList[index] = book
            val editor = sharedPreferences.edit()
            editor.putString("books", Gson().toJson(bookList))
            editor.apply()

            val newsItem = NewsItem(
                type = "quote_added",
                bookTitle = book.title,
                bookAuthor = book.author,
                timestamp = System.currentTimeMillis()
            )
            addNewsItem(newsItem)

            Toast.makeText(this, "Книга обновлена", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Книга не найдена", Toast.LENGTH_SHORT).show()
        }
    }

    // Диалоговое окно для редактирования и удаления цитаты
    private fun showEditQuoteDialog(position: Int) {
        val input = EditText(this)
        // Предзаполнение текста цитаты
        input.setText(quoteList[position])

        val dialog = AlertDialog.Builder(this)
            .setTitle("Редактировать цитату")
            .setView(input)
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedQuote = input.text.toString()
                if (updatedQuote.isNotEmpty()) {
                    quoteList[position] = updatedQuote
                    book.quotes = quoteList
                    saveQuotes()
                    quoteAdapter.notifyDataSetChanged()
                }
            }
            .setNeutralButton("Удалить") { _, _ ->
                quoteList.removeAt(position)
                book.quotes = quoteList
                saveQuotes()
                quoteAdapter.notifyDataSetChanged()
            }
            .setNegativeButton("Отмена", null)
            .create()
        dialog.show()
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

    // Загрузка цитат
    private fun loadQuotes() {
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<MutableList<Book>>() {}.type
        val bookList = Gson().fromJson<MutableList<Book>>(json, type)

        val currentBook = bookList.find { it.title == book.title && it.author == book.author }

        if (currentBook != null) {
            quoteList.clear()
            quoteList.addAll(currentBook.quotes)
            quoteAdapter.notifyDataSetChanged()
        }
    }

    // Загрузка фото
    private fun loadPhotos() {
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val json = sharedPreferences.getString("photos_${book.title}", "[]")
        val type = object : TypeToken<MutableList<String>>() {}.type
        photoList.clear()
        photoList.addAll(Gson().fromJson(json, type))
        photoAdapter.notifyDataSetChanged()
    }

    // Сохранение фото
    private fun savePhotos() {
        val sharedPreferences = getSharedPreferences("BookData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(photoList)
        editor.putString("photos_${book.title}", json)
        editor.apply()

        val newsItem = NewsItem(
            type = "photo_added",
            bookTitle = book.title,
            bookAuthor = book.author,
            photoUrl = json,
            timestamp = System.currentTimeMillis()
        )
        addNewsItem(newsItem)
    }

    // Сохранение фото в галерею
    private fun savePhotoToGallery(context: Context, photoPath: String): Uri? {
        val photoFile = File(photoPath)
        if (!photoFile.exists()) {
            return null
        }

        val correctedBitmap = correctImageOrientation(photoPath)

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        return imageUri
    }

    // Редактирование фото перед отправкой в галерею
    private fun correctImageOrientation(photoPath: String): Bitmap {
        val bitmap = BitmapFactory.decodeFile(photoPath)
        val exif = ExifInterface(photoPath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoList.add(it.toString())
            savePhotos()
            photoAdapter.notifyDataSetChanged()
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && currentPhotoPath != null) {
            val photoUri = savePhotoToGallery(this, currentPhotoPath!!)
            if (photoUri != null) {
                Toast.makeText(this, "Фото сохранено в галерею", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ошибка при сохранении фото в галерею", Toast.LENGTH_SHORT).show()
            }

            photoList.add(currentPhotoPath!!)
            savePhotos()
            photoAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Фото не было сделано", Toast.LENGTH_SHORT).show()
        }
    }
}