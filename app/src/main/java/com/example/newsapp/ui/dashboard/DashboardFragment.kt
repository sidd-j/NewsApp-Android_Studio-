package com.example.newsapp.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.databinding.FragmentDashboardBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import android.util.Log
import kotlinx.coroutines.delay


class DashboardFragment : Fragment() {

    private val articlesList = mutableListOf<NewsArticle>()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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

    private suspend fun fetchNewsArticles(apiKey: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val urlScience = URL("https://api.nytimes.com/svc/topstories/v2/science.json?api-key=$apiKey")
                val scienceResponse = urlScience.readText()

                val urlTechnology = URL("https://api.nytimes.com/svc/topstories/v2/world.json?api-key=$apiKey")
                val techResponse = urlTechnology.readText()

                val urlPolitics = URL("https://api.nytimes.com/svc/topstories/v2/politics.json?api-key=$apiKey")
                val politicsResponse = urlPolitics.readText()

                // Introduce a delay of 12 seconds between API calls to avoid rate limit

                delay(12)
                listOf(techResponse,scienceResponse, politicsResponse)
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList() // Return an empty list if there's an error
            }
        }
    }



    private fun handleNewsApiResponse(responses: List<String>) {
        val techArticles = mutableListOf<NewsArticle>()
        val scienceArticles = mutableListOf<NewsArticle>()
        val politicsArticles = mutableListOf<NewsArticle>()


        for (response in responses) {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.optJSONArray("results")
            println("REsult array"+resultsArray)

            if (resultsArray != null) {
                for (i in 0 until resultsArray.length()) {
                    val articleObject = resultsArray.getJSONObject(i)
                    val mediaArray = articleObject.optJSONArray("multimedia")


                    if (mediaArray != null && mediaArray.length() > 0) {
                        val media = mediaArray.getJSONObject(0)
                        val imageUrl = media.optString("url")
                        val title = articleObject.optString("title")

                        if (title.isNotBlank() && imageUrl.isNotBlank()) {
                            val article = NewsArticle(title, imageUrl)
                            when {
                                response.contains("science") -> {
                                    if (scienceArticles.size < 5) {
                                        scienceArticles.add(article)
                                    }
                                }
                                response.contains("World") -> {
                                    if (techArticles.size < 5) {
                                        techArticles.add(article)
                                    }

                                }
                                response.contains("politics") -> {
                                    if (politicsArticles.size < 5) {
                                        politicsArticles.add(article)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Update UI with articles for each category
        updateUIforCategory(scienceArticles, "science")
        updateUIforCategory(politicsArticles, "politics")
        updateUIforCategory(techArticles, "world")
    }


    private fun updateUIforCategory(articles: List<NewsArticle>, category: String) {
        when (category) {
            "science" -> {
                articles.forEachIndexed { index, article ->
                    if (index < 5) {
                        // Update UI for science category
                        val cardView = binding.root.findViewById<CardView>(
                            resources.getIdentifier(
                                "SciencearticleC${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val titleTextView = cardView.findViewById<TextView>(
                            resources.getIdentifier(
                                "SciencearticleTitleC${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val imageView = cardView.findViewById<ImageView>(
                            resources.getIdentifier(
                                "SciencearticleImageViewC${index + 1}", "id", requireContext().packageName
                            )
                        )

                        titleTextView.text = article.title

                        // Load the image using Picasso
                        Picasso.get()
                            .load(article.imageUrl)
                            .fit()
                            .centerCrop()
                            .into(imageView)

                        cardView.visibility = View.VISIBLE
                    }
                }
            }

            "politics" -> {
                articles.forEachIndexed { index, article ->
                    if (index < 5) {
                        // Update UI for politics category
                        val cardView = binding.root.findViewById<CardView>(
                            resources.getIdentifier(
                                "Politicsarticle${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val titleTextView = cardView.findViewById<TextView>(
                            resources.getIdentifier(
                                "PoliticsarticleTitle${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val imageView = cardView.findViewById<ImageView>(
                            resources.getIdentifier(
                                "PoliticsarticleImageView${index + 1}", "id", requireContext().packageName
                            )
                        )

                        titleTextView.text = article.title

                        // Load the image using Picasso
                        Picasso.get()
                            .load(article.imageUrl)
                            .fit()
                            .centerCrop()
                            .into(imageView)

                        cardView.visibility = View.VISIBLE
                    }
                }
            }
            "world" -> {
                articles.forEachIndexed { index, article ->
                    println("Technology section")

                    if (index < 5) {
                        // Update UI for sports category
                        val cardView = binding.root.findViewById<CardView>(
                            resources.getIdentifier(
                                "TecharticleB${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val titleTextView = cardView.findViewById<TextView>(
                            resources.getIdentifier(

                                "TecharticleTitleB${index + 1}", "id", requireContext().packageName
                            )
                        )
                        val imageView = cardView.findViewById<ImageView>(
                            resources.getIdentifier(
                                "TecharticleImageViewB${index + 1}", "id", requireContext().packageName
                            )
                        )

                        titleTextView.text = article.title

                        // Load the image using Picasso
                        Picasso.get()
                            .load(article.imageUrl)
                            .fit()
                            .centerCrop()
                            .into(imageView)

                        cardView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }





}
