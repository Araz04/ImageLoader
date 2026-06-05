package com.task.imageloader.sample.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageItem(
    @param:Json(name = "id") val id: Int,
    @param:Json(name = "imageUrl") val imageUrl: String
)
