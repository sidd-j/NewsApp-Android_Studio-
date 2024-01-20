package com.example.newsapp.ui.notifications




data class NewsArticle(
    val title: String,
    val imageUrl: String,
    val abstract: String,
    val url: String,
    val multimedia: List<Multimedia>,
    val datepublish : String
)