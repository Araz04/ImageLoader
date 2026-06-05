package com.task.imageloader.sample.repository

import com.task.imageloader.sample.model.ImageItem
import retrofit2.http.GET

interface ApiService {
    @GET("image_list.json")
    suspend fun getImageList(): List<ImageItem>
}
