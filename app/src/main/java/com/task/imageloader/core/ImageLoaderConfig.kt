package com.task.imageloader.core

import com.task.imageloader.cache.CachePolicy

data class ImageLoaderConfig(
    val memoryCacheSizePercent: Int = 8,
    val diskCacheMaxAgeMillis: Long =
        CachePolicy.DEFAULT_TTL_MS
)