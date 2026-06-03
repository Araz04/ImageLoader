package com.ideascollection.imageloader.core

data class ImageRequest(
    val url: String,
    val placeholderResId: Int,
    val targetWidth: Int? = null,
    val targetHeight: Int? = null
)