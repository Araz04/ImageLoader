package com.task.imageloader.core

import android.widget.ImageView
import androidx.annotation.DrawableRes

data class ImageRequest private constructor(
    val url: String,
    val target: ImageView,
    @DrawableRes val placeholderRes: Int?
) {

    class Builder(private val target: ImageView) {
        private var url: String = ""
        private var placeholderRes: Int? = null

        fun url(url: String) = apply { this.url = url }

        fun placeholder(@DrawableRes resId: Int) = apply { this.placeholderRes = resId }

        fun build(): ImageRequest {
            require(url.isNotBlank()) { "URL must not be blank" }
            return ImageRequest(url = url, target = target, placeholderRes = placeholderRes)
        }
    }
}