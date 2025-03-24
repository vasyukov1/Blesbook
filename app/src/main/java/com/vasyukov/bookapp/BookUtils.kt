package com.vasyukov.bookapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream

object BookUtils {
    // Получение всех книг
    fun getAllBooks(context: Context): List<Book> {
        val sharedPreferences = context.getSharedPreferences("BookData", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("books", "[]")
        val type = object : TypeToken<List<Book>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Экспорт книг в JSON-файл
    fun exportBooksToJson(context: Context): Boolean {
        val allBooks = getAllBooks(context)

        val gson = Gson()
        val json = gson.toJson(allBooks)

        return try {
            val file = File(context.getExternalFilesDir(null), "books_export.json")
            val outputStream = FileOutputStream(file)
            outputStream.write(json.toByteArray())
            outputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}