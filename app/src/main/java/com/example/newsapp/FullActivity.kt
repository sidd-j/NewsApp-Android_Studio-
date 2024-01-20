package com.example.newsapp


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class FullActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full)

        // Retrieve article data from intent extras
        val articleTitle = intent.getStringExtra("articleTitle")
        val articleContent = intent.getStringExtra("articleContent")
        val articleimage = intent.getStringExtra("articleImageUrl")
        val articleurl = intent.getStringExtra("FullArticle")

        // Populate UI with article data
        val titleTextView = findViewById<TextView>(R.id.ATV)
        val contentTextView = findViewById<TextView>(R.id.ATVD)
        titleTextView.text = articleTitle
        contentTextView.text = articleContent

        val imageView = findViewById<ImageView>(R.id.imageFA)
        if (!articleimage.isNullOrEmpty()) {
            Picasso.get().load(articleimage).into(imageView)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }


        val bttn = findViewById<Button>(R.id.fullAB)
        bttn.setOnClickListener(){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleurl))
            startActivity(intent)

        }
    }
}
