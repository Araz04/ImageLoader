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



    fun get(key: String): Bitmap? {
        val imgFile = bitmapFile(key)
        val metaFile = metaFile(key)

        if (!imgFile.exists() || !metaFile.exists()) return null


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

    fun put(key: String, bitmap: Bitmap) {
        try {
            val imgFile = bitmapFile(key)
            FileOutputStream(imgFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            val expiryTime = System.currentTimeMillis() + ttlMillis
            metaFile(key).writeText(expiryTime.toString())
        } catch (e: Exception) {
        }
    }


    fun remove(key: String) {
        bitmapFile(key).delete()
        metaFile(key).delete()
    }

    fun clear() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}