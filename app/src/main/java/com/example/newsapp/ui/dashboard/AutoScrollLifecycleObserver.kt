package com.example.newsapp.ui.dashboard




import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2

class AutoScrollLifecycleObserver(private val viewPager: ViewPager2) : LifecycleObserver {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            val currentItem = viewPager.currentItem
            val totalItems = viewPager.adapter?.itemCount ?: 0
            if (currentItem < totalItems - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                viewPager.setCurrentItem(0, true)
            }
            handler.postDelayed(this, 3000) // 3 seconds delay for next slide
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startAutoScroll() {
        handler.postDelayed(runnable, 3000) // Start the first slide after 3 seconds
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopAutoScroll() {
        handler.removeCallbacks(runnable) // Stop the automatic sliding when the fragment is paused
    }
}
