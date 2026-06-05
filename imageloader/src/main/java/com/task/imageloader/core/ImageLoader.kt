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
    private val activeJobs = HashMap<ImageView, Job>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)


    @JvmOverloads
    fun load(
        target: ImageView,
        url: String,
        @DrawableRes placeholderRes: Int? = null,
    ) {
        val request = ImageRequest.Builder(target)
            .url(url)
            .apply { placeholderRes?.let { placeholder(it) } }
            .build()
        load(request)
    }


    fun load(request: ImageRequest) {
        val target = request.target

        activeJobs[target]?.cancel()

        request.placeholderRes?.let { target.setImageResource(it) }

        val cacheKey = cacheKeyFor(request.url)

        val job = scope.launch {
            val cached = memoryCache.get(cacheKey)
            if (cached != null) {
                target.setImageBitmap(cached)
                return@launch
            }

            val fromDisk = withContext(Dispatchers.IO) { diskCache.get(cacheKey) }
            if (fromDisk != null) {
                memoryCache.put(cacheKey, fromDisk)
                target.setImageBitmap(fromDisk)
                return@launch
            }

            val bitmap: Bitmap? = downloader.download(request.url)
            if (bitmap != null) {
                memoryCache.put(cacheKey, bitmap)
                withContext(Dispatchers.IO) { diskCache.put(cacheKey, bitmap) }
                if (activeJobs[target] == this.coroutineContext[Job]) {
                    target.setImageBitmap(bitmap)
                }
            }
        }
        activeJobs[target] = job
    }

    fun invalidate(url: String) {
        val key = cacheKeyFor(url)
        memoryCache.remove(key)
        diskCache.remove(key)
    }

    fun clearCache() {
        memoryCache.clear()
        diskCache.clear()
    }

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