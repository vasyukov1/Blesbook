package com.vasyukov.bookapp

import java.util.UUID

data class NewsItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val bookTitle: String? = null,
    val bookAuthor: String? = null,
    val quote: String? = null,
    val photoUrl: String? = null,
    val timestamp: Long,
    var isRead: Boolean = false
)