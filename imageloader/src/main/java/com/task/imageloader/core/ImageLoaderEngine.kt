package com.task.imageloader.core

interface ImageLoaderEngine {
    suspend fun load(
        request: ImageRequest
    ): ImageResult

    fun clearCache()
}