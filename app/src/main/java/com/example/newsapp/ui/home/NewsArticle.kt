package com.example.newsapp.ui.home




data class NewsArticle(
    val title: String,
    val imageUrl: String,
    val abstract: String,
    val url: String,
    val multimedia: List<Multimedia>,
    val hoursAgo: String
)