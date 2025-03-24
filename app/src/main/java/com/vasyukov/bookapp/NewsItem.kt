package com.vasyukov.bookapp

data class NewsItem(
    val type: String,
    val bookTitle: String,
    val bookAuthor: String,
    val quote: String? = null,
    val photoUrl: String? = null,
    val timestamp: Long
)