package com.task.imageloader.sample.repository

import com.task.imageloader.sample.model.ImageItem

class ImageRepository(private val apiService: ApiService) {
    suspend fun fetchImages(): List<ImageItem> = apiService.getImageList()
}