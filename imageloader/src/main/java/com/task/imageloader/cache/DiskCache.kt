package com.task.imageloader.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

internal class DiskCache(
    private val cacheDir: File,
    private val ttlMillis: Long = CachePolicy.DEFAULT_TTL_MS
) {

    init {
        if (!cacheDir.exists()) cacheDir.mkdirs()
    }

    private fun bitmapFile(key: String) = File(cacheDir, "$key.jpg")
    private fun metaFile(key: String) = File(cacheDir, "$key.meta")

    /**
     * Returns the cached [Bitmap] for [key], or null if it doesn't exist or has expired.
     */
    fun get(key: String): Bitmap? {
        val imgFile = bitmapFile(key)
        val metaFile = metaFile(key)

        if (!imgFile.exists() || !metaFile.exists()) return null

        // Check TTL
        val expiryTime = metaFile.readText().trim().toLongOrNull() ?: return null
        if (System.currentTimeMillis() > expiryTime) {
            imgFile.delete()
            metaFile.delete()
            return null
        }

        return try {
            BitmapFactory.decodeFile(imgFile.absolutePath)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Persists [bitmap] to disk under [key] and writes the expiry metadata.
     */
    fun put(key: String, bitmap: Bitmap) {
        try {
            val imgFile = bitmapFile(key)
            FileOutputStream(imgFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            val expiryTime = System.currentTimeMillis() + ttlMillis
            metaFile(key).writeText(expiryTime.toString())
        } catch (e: Exception) {
            // Silently ignore disk write failures; memory cache still works
        }
    }

    /**
     * Removes cached files for the given [key].
     */
    fun remove(key: String) {
        bitmapFile(key).delete()
        metaFile(key).delete()
    }

    /**
     * Deletes all cached files from the cache directory.
     */
    fun clear() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}