// NewsAdapter2.kt
package com.example.newsapp.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.squareup.picasso.Picasso

class NewsAdapter(private val articles: MutableList<com.example.newsapp.ui.home.NewsArticle>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_article, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentArticle = articles[position]

        holder.titleTextView.text = currentArticle.title
        holder.datepublish.text = currentArticle.hoursAgo
        // Check if the image URL is not empty or null before loading
        if (!currentArticle.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(currentArticle.imageUrl).into(holder.imageView)
            holder.imageView.visibility = View.VISIBLE
        } else {
            // Handle the case where the image URL is empty or null
            // For example, you can set a placeholder image or hide the ImageView
            holder.imageView.visibility = View.GONE

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentArticle.url))
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return articles.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.articleTitleTextView)
        val imageView: ImageView = itemView.findViewById(R.id.articleImageView)
        val datepublish:TextView = itemView.findViewById(R.id.articleDatePublish)
    }
}
