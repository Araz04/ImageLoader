package com.task.imageloader.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

internal class ImageDownloader {

    companion object {
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 30_000
    }

    /**
     * Downloads the image at [url] and returns the decoded [Bitmap], or null on failure.
     */
    suspend fun download(url: String): Bitmap? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                requestMethod = "GET"
                doInput = true
                connect()
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            BitmapFactory.decodeStream(connection.inputStream)
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}