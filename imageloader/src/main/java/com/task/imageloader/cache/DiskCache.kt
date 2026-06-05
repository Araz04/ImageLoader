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
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    private fun dataFile(key: String) =
        File(cacheDir, "$key.data")

    private fun metaFile(key: String) =
        File(cacheDir, "$key.meta")

    fun get(key: String): ByteArray? {

        val dataFile = dataFile(key)
        val metaFile = metaFile(key)

        if (!dataFile.exists() || !metaFile.exists()) {
            return null
        }

        val expiryTime =
            metaFile.readText()
                .trim()
                .toLongOrNull()
                ?: return null

        if (System.currentTimeMillis() > expiryTime) {
            dataFile.delete()
            metaFile.delete()
            return null
        }

        return try {
            dataFile.readBytes()
        } catch (e: Exception) {
            null
        }
    }

    fun put(
        key: String,
        bytes: ByteArray
    ) {

        try {

            dataFile(key).writeBytes(bytes)

            val expiryTime =
                System.currentTimeMillis() + ttlMillis

            metaFile(key)
                .writeText(expiryTime.toString())

        } catch (_: Exception) {
        }
    }

    fun remove(key: String) {
        dataFile(key).delete()
        metaFile(key).delete()
    }

    fun clear() {
        cacheDir.listFiles()?.forEach {
            it.delete()
        }
    }
}