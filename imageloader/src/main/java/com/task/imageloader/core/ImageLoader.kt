package com.task.imageloader.core

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.task.imageloader.cache.BitmapDecoder
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
            .resize(300, 300)
            .apply { placeholderRes?.let { placeholder(it) } }
            .build()
        load(request)
    }


    fun load(request: ImageRequest) {

        val target = request.target

        activeJobs[target]?.cancel()

        request.placeholderRes?.let {
            target.setImageResource(it)
        }

        val cacheKey = cacheKeyFor(request.url)

        val job = scope.launch {

            //
            // MEMORY CACHE
            //
            memoryCache.get(cacheKey)?.let { bitmap ->
                target.setImageBitmap(bitmap)
                return@launch
            }

            val reqWidth = resolveTargetWidth(request)
            val reqHeight = resolveTargetHeight(request)

            //
            // DISK CACHE
            //
            val cachedBytes = withContext(Dispatchers.IO) {
                diskCache.get(cacheKey)
            }

            if (cachedBytes != null) {

                val bitmap = withContext(Dispatchers.Default) {
                    BitmapDecoder.decode(
                        bytes = cachedBytes,
                        reqWidth = reqWidth,
                        reqHeight = reqHeight
                    )
                }

                if (bitmap != null) {
                    memoryCache.put(cacheKey, bitmap)

                    if (activeJobs[target] == coroutineContext[Job]) {
                        target.setImageBitmap(bitmap)
                    }

                    return@launch
                }
            }

            //
            // NETWORK
            //
            val downloadedBytes = downloader.download(request.url)

            if (downloadedBytes == null) {
                return@launch
            }

            withContext(Dispatchers.IO) {
                diskCache.put(
                    key = cacheKey,
                    bytes = downloadedBytes
                )
            }

            val bitmap = withContext(Dispatchers.Default) {
                BitmapDecoder.decode(
                    bytes = downloadedBytes,
                    reqWidth = reqWidth,
                    reqHeight = reqHeight
                )
            }

            if (bitmap != null) {

                memoryCache.put(
                    key = cacheKey,
                    bitmap = bitmap
                )

                if (activeJobs[target] == coroutineContext[Job]) {
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

    private fun resolveTargetWidth(request: ImageRequest): Int {
        return request.overrideWidth
            ?: request.target.width.takeIf { it > 0 }
            ?: 300
    }

    private fun resolveTargetHeight(request: ImageRequest): Int {
        return request.overrideHeight
            ?: request.target.height.takeIf { it > 0 }
            ?: 300
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