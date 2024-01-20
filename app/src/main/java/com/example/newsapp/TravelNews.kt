package com.example.newsapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityTravelNewsBinding
import com.example.newsapp.ui.notifications.NewsAdapterTech
import com.example.newsapp.ui.notifications.NewsArticle
import com.example.newsapp.ui.notifications.Multimedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.time.*
import java.time.format.*
class TravelNews : AppCompatActivity() {
    private val articlesList = mutableListOf<NewsArticle>()
    private lateinit var binding: ActivityTravelNewsBinding

    private lateinit var adapter: NewsAdapterTech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NewsAdapterTech(articlesList)
        binding.TravelRecycleView.adapter = adapter
        binding.TravelRecycleView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                val apiKey = "rZcS3xlAHpqx9P85OIrT9vvh08YLFQGb"
                val response = fetchNewsArticles(apiKey)
                handleNewsApiResponse(response)
                delay(12000)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    private suspend fun fetchNewsArticles(apiKey: String): String {
        val url = URL("https://api.nytimes.com/svc/topstories/v2/travel.json?api-key=$apiKey")
        return withContext(Dispatchers.IO) {
            try {
                url.readText()
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }
    }

    private fun handleNewsApiResponse(response: String) {
        val jsonObject = JSONObject(response)
        val resultsArray = jsonObject.optJSONArray("results")

        if (resultsArray != null) {
            for (i in 0 until resultsArray.length()) {
                val articleObject = resultsArray.getJSONObject(i)
                val title = articleObject.optString("title", "")
                val abstract = articleObject.optString("abstract", "")
                val url = articleObject.optString("url", "")
                val multimediaArray = articleObject.optJSONArray("multimedia")
                val datepublish = articleObject.optString(("published_date"))
                val dateTime = OffsetDateTime.parse(datepublish)
                val hoursAgo = Duration.between(dateTime.toInstant(), Instant.now()).toHours()


                val multimediaList = mutableListOf<Multimedia>()
                multimediaArray?.let {
                    for (j in 0 until it.length()) {
                        val multimediaObject = it.getJSONObject(j)
                        val multimediaUrl = multimediaObject.optString("url", "")
                        val format = multimediaObject.optString("format", "")
                        val height = multimediaObject.optInt("height", 0)
                        val width = multimediaObject.optInt("width", 0)
                        multimediaList.add(Multimedia(multimediaUrl, format, height, width))
                    }
                }

                if (title.isNotBlank() && abstract.isNotBlank() && url.isNotBlank()) {
                    // Here, imageUrl should be the first multimedia url if available
                    val imageUrl = multimediaList.firstOrNull()?.url ?: ""
                    articlesList.add(NewsArticle(title, imageUrl, abstract, url, multimediaList,hoursAgo.toString()))
                }
            }

            adapter.notifyDataSetChanged()
        }
    }
}
