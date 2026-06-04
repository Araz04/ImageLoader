package com.task.imageloader.cache

import android.graphics.Bitmap

internal data class CacheEntry(
    val bitmap: Bitmap,
    val expiryTime: Long
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiryTime
}
