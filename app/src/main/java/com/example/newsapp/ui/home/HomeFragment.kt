package com.example.newsapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.databinding.FragmentHomeBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.io.IOException

class HomeFragment : Fragment() {
    private val articlesList = mutableListOf<NewsArticle>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch news articles using coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiKey = "rZcS3xlAHpqx9P85OIrT9vvh08YLFQGb"

                val response = fetchNewsArticles(apiKey)
                handleNewsApiResponse(response)
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun fetchNewsArticles(apiKey: String): String {
        val url = URL("https://api.nytimes.com/svc/mostpopular/v2/emailed/30.json?api-key=$apiKey")
        return withContext(Dispatchers.IO) {
            try {
                url.readText()
            } catch (e: IOException) {
                // Handle IO exception
                e.printStackTrace()
                ""
            }
        }
    }

    private fun updateUI() {
        for ((index, article) in articlesList.withIndex()) {
            if (index < 5) { // Assuming you want to display only 5 articles
                val cardView = binding.root.findViewById<CardView>(
                    resources.getIdentifier(
                        "article${index + 1}",
                        "id",
                        requireContext().packageName
                    )
                )
                val titleTextView = cardView.findViewById<TextView>(
                    resources.getIdentifier(
                        "titlesTextView${index + 1}",
                        "id",
                        requireContext().packageName
                    )
                )
                val imageView = cardView.findViewById<ImageView>(
                    resources.getIdentifier(
                        "articleImageView${index + 1}",
                        "id",
                        requireContext().packageName
                    )
                )

                titleTextView.text = article.title

                // Load the image using Picasso
                //Picasso.get()
                //  .load(article.imageUrl)
                //.into(imageView)
                Picasso.get()
                    .load(article.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imageView)

                cardView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleNewsApiResponse(response: String) {
        // Parse JSON response and update UI
        val jsonObject = JSONObject(response)
        val resultsArray = jsonObject.optJSONArray("results")

        if (resultsArray != null) {
            for (i in 0 until resultsArray.length()) {
                val articleObject = resultsArray.getJSONObject(i)
                val mediaArray = articleObject.optJSONArray("media")

                if (mediaArray != null && mediaArray.length() > 0) {
                    val media = mediaArray.getJSONObject(0)
                    val mediaMetadataArray = media.optJSONArray("media-metadata")

                    if (mediaMetadataArray != null && mediaMetadataArray.length() > 0) {
                        // Find the image with the highest resolution (assuming the list is not empty)
                        var highQualityImage: JSONObject? = null
                        for (j in 0 until mediaMetadataArray.length()) {
                            val metadata = mediaMetadataArray.getJSONObject(j)
                            if (highQualityImage == null ||
                                metadata.optInt("width", 0) * metadata.optInt("height", 0) >
                                highQualityImage.optInt(
                                    "width",
                                    0
                                ) * highQualityImage.optInt("height", 0)
                            ) {
                                highQualityImage = metadata
                            }
                        }

                        // Get the URL of the high-quality image
                        val highQualityImageUrl = highQualityImage?.optString("url", "")
                        val title = articleObject.optString("title", "")

                        if (title.isNotBlank() && highQualityImageUrl != null && highQualityImageUrl.isNotBlank()) {
                            articlesList.add(NewsArticle(title, highQualityImageUrl))
                        }
                    }
                }
            }

            updateUI()
        }
    }
}
