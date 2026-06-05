package com.task.imageloader.core

import android.graphics.Bitmap

sealed class ImageResult {
    data class Success(val bitmap: Bitmap) : ImageResult()
    data class Error(val throwable: Throwable) : ImageResult()
}