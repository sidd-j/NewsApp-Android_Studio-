package com.example.newsapp.ui.dashboard

// NewsAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.ui.dashboard.NewsArticle
import com.squareup.picasso.Picasso

class NewsAdapter(private val articles: List<NewsArticle>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImageView: ImageView = itemView.findViewById(R.id.articleImageView)
        val articleTitleTextView: TextView = itemView.findViewById(R.id.articleTitleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        holder.articleTitleTextView.text = article.title

        // Load the image using Picasso
        Picasso.get()
            .load(article.imageUrl)
            .fit()
            .centerCrop()
            .into(holder.articleImageView)
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}
