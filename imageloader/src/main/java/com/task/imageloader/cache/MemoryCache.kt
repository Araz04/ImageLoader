package com.task.imageloader.cache

import android.graphics.Bitmap
import android.util.LruCache
import com.task.imageloader.cache.CachePolicy.DEFAULT_TTL_MS

internal class MemoryCache(
    private val ttlMillis: Long = DEFAULT_TTL_MS
) {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8

    private val lruCache = object : LruCache<String, CacheEntry>(cacheSize) {
        override fun sizeOf(
            key: String,
            value: CacheEntry
        ): Int {
            return value.bitmap.byteCount / 1024
        }
    }

    fun get(key: String): Bitmap? {
        val entry = lruCache.get(key) ?: return null

        if (entry.isExpired) {
            lruCache.remove(key)
            return null
        }

        return entry.bitmap
    }

    fun put(
        key: String,
        bitmap: Bitmap
    ) {
        val entry = CacheEntry(
            bitmap = bitmap,
            expiryTime = System.currentTimeMillis() + ttlMillis
        )

        lruCache.put(key, entry)
    }

    fun remove(key: String) {
        lruCache.remove(key)
    }

    fun clear() {
        lruCache.evictAll()
    }
}