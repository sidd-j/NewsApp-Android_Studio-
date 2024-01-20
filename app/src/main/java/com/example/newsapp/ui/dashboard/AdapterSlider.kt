package com.example.newsapp.ui.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.newsapp.FullActivity
import com.example.newsapp.R
import com.example.newsapp.ui.notifications.NewsArticle
import com.squareup.picasso.Picasso

class AdapterSlider(private val context: Context, private val articles: MutableList<NewsArticle>) : RecyclerView.Adapter<AdapterSlider.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_slider, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        // Bind data to views
        holder.titleTextView.text = article.title
        holder.datePublish.text = article.datepublish

        // Load image using Picasso
        if (!article.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(article.imageUrl).into(holder.imageView)
            holder.imageView.visibility = View.VISIBLE
        } else {
            holder.imageView.visibility = View.GONE
        }

        // Set click listener to open article URL in browser
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullActivity::class.java).apply {
                putExtra("articleTitle", article.title)
                putExtra("articleContent", article.abstract)
                putExtra("articleImageUrl",article.imageUrl)
                putExtra("FullArticle",article.url)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    // ViewHolder class to hold item views
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.articleTitleTextView) ?: throw NullPointerException("Title TextView not found")
        val imageView: ImageView = itemView.findViewById(R.id.articleImageView) ?: throw NullPointerException("Image ImageView not found")
        val datePublish: TextView = itemView.findViewById(R.id.articleDatePublish) ?: throw NullPointerException("Date TextView not found")
    }
}
