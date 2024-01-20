package com.example.newsapp.ui.dashboard

import ImageSliderAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapp.Politics
import com.example.newsapp.R
import com.example.newsapp.ScienceNews
import com.example.newsapp.WorldNews
import com.example.newsapp.databinding.FragmentDashboardBinding
import com.example.newsapp.ui.notifications.AdapterHorizontal
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
import java.util.Timer
import java.util.TimerTask

class DashboardFragment : Fragment() {
    private val politicsArticlesList = mutableListOf<NewsArticle>()
    private val scienceArticlesList = mutableListOf<NewsArticle>()
    private val sportsArticlesList = mutableListOf<NewsArticle>()
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var politicsAdapter: AdapterHorizontal
    private lateinit var scienceAdapter: AdapterHorizontal
    private lateinit var sportsAdapter: AdapterHorizontal
    private lateinit var viewPager: ViewPager2
    private lateinit var timer: Timer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        politicsAdapter = AdapterHorizontal(politicsArticlesList)
        binding.PoliticsRecylerView.adapter = politicsAdapter
        binding.PoliticsRecylerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        scienceAdapter = AdapterHorizontal(scienceArticlesList)
        binding.ScienceRecylerView.adapter = scienceAdapter
        binding.ScienceRecylerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        sportsAdapter = AdapterHorizontal(sportsArticlesList)
        binding.WorldRecylerView.adapter = sportsAdapter
        binding.WorldRecylerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        val sciencebttn = view.findViewById<Button>(R.id.sciencebttn)

        sciencebttn.setOnClickListener {
            val intent = Intent(requireContext(), ScienceNews::class.java)
            startActivity(intent)
        }

        val polibttn = view.findViewById<Button>(R.id.politicsbttn)

        polibttn.setOnClickListener {
            val intent = Intent(requireContext(), Politics::class.java)
            startActivity(intent)
        }

        val worldbttn = view.findViewById<Button>(R.id.worldbttn)

        worldbttn.setOnClickListener {
            val intent = Intent(requireContext(), WorldNews::class.java)
            startActivity(intent)
        }

        viewPager = view.findViewById(R.id.viewPager)

        // Replace with your actual image resources
        val images = listOf(R.drawable.worldmap, R.drawable.sports_categories, R.drawable.politic)

        // Set up your ViewPager2 adapter here
        val adapter = ImageSliderAdapter(images)
        viewPager.adapter = adapter

        // Start the timer to auto-scroll the ViewPager2
        startTimer()

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val apiKey = "rZcS3xlAHpqx9P85OIrT9vvh08YLFQGb"
                val politicsResponse = fetchNewsArticles(apiKey, "politics")
                handleNewsApiResponse(politicsResponse, politicsArticlesList)

                val scienceResponse = fetchNewsArticles(apiKey, "science")
                handleNewsApiResponse(scienceResponse, scienceArticlesList)

                val sportsResponse = fetchNewsArticles(apiKey, "sports")
                handleNewsApiResponse(sportsResponse, sportsArticlesList)

                delay(12000)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    private suspend fun fetchNewsArticles(apiKey: String, category: String): String {
        val url = URL("https://api.nytimes.com/svc/topstories/v2/$category.json?api-key=$apiKey")
        return withContext(Dispatchers.IO) {
            try {
                url.readText()
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }
    }

    private fun handleNewsApiResponse(response: String, articlesList: MutableList<NewsArticle>) {
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
                    articlesList.add(NewsArticle(title, imageUrl, abstract, url, multimediaList, hoursAgo.toString()))
                }
            }

            // Notify the adapter that the data set has changed
            politicsAdapter.notifyDataSetChanged()
            scienceAdapter.notifyDataSetChanged()
            sportsAdapter.notifyDataSetChanged()
        }
    }
    private fun startTimer() {
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (viewPager.currentItem < 3 - 1) {
                viewPager.currentItem += 1
            } else {
                viewPager.currentItem = 0
            }
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 3000) // Delay 3 seconds, repeat every 3 seconds
    }
}
