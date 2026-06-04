package com.task.imageloader.core

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.task.imageloader.cache.DiskCache
import com.task.imageloader.cache.MemoryCache
import com.task.imageloader.network.ImageDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

class ImageLoader private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val memoryCache = MemoryCache()
    private val diskCache = DiskCache(File(appContext.cacheDir, "image_loader_cache"))
    private val downloader = ImageDownloader()

    /** Tracks the active load job per ImageView to cancel stale requests on recycled views. */
    private val activeJobs = HashMap<ImageView, Job>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Loads an image from [url] into [target], optionally showing [placeholderRes] during load.
     *
     * This method is safe to call from the main thread and handles view recycling correctly.
     */
    @JvmOverloads
    fun load(
        target: ImageView,
        url: String,
        @DrawableRes placeholderRes: Int? = null
    ) {
        val request = ImageRequest.Builder(target)
            .url(url)
            .apply { placeholderRes?.let { placeholder(it) } }
            .build()
        load(request)
    }

    /**
     * Loads an image described by [request].
     */
    fun load(request: ImageRequest) {
        val target = request.target

        // Cancel any in-flight request for this view
        activeJobs[target]?.cancel()

        // Show placeholder immediately
        request.placeholderRes?.let { target.setImageResource(it) }

        val cacheKey = cacheKeyFor(request.url)

        val job = scope.launch {
            // 1. Check memory cache (already on Main, quick check)
            val cached = memoryCache.get(cacheKey)
            if (cached != null) {
                target.setImageBitmap(cached)
                return@launch
            }

            // 2. Check disk cache on IO thread
            val fromDisk = withContext(Dispatchers.IO) { diskCache.get(cacheKey) }
            if (fromDisk != null) {
                memoryCache.put(cacheKey, fromDisk)
                target.setImageBitmap(fromDisk)
                return@launch
            }

            // 3. Download
            val bitmap: Bitmap? = downloader.download(request.url)
            if (bitmap != null) {
                memoryCache.put(cacheKey, bitmap)
                withContext(Dispatchers.IO) { diskCache.put(cacheKey, bitmap) }
                // Only update the view if this job is still the active one for this target
                if (activeJobs[target] == this.coroutineContext[Job]) {
                    target.setImageBitmap(bitmap)
                }
            }
        }
        activeJobs[target] = job
    }

    /**
     * Removes all cached data for [url] from both memory and disk caches.
     */
    fun invalidate(url: String) {
        val key = cacheKeyFor(url)
        memoryCache.remove(key)
        diskCache.remove(key)
    }

    /**
     * Clears the entire cache (both memory and disk).
     */
    fun clearCache() {
        memoryCache.clear()
        diskCache.clear()
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private fun cacheKeyFor(url: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(url.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {
        @Volatile
        private var instance: ImageLoader? = null

        @JvmStatic
        fun getInstance(context: Context): ImageLoader =
            instance ?: synchronized(this) {
                instance ?: ImageLoader(context).also { instance = it }
            }
    }
}