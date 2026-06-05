package com.task.imageloader.core

import android.widget.ImageView
import androidx.annotation.DrawableRes

data class ImageRequest private constructor(
    val url: String,
    val target: ImageView,
    @DrawableRes val placeholderRes: Int?,
    val overrideWidth: Int?,
    val overrideHeight: Int?
) {

    class Builder(private val target: ImageView) {

        private var url: String = ""
        private var placeholderRes: Int? = null

        private var overrideWidth: Int? = null
        private var overrideHeight: Int? = null

        fun url(url: String) = apply {
            this.url = url
        }

        fun placeholder(@DrawableRes resId: Int) = apply {
            placeholderRes = resId
        }

        fun resize(width: Int, height: Int) = apply {
            overrideWidth = width
            overrideHeight = height
        }

        fun build(): ImageRequest {
            require(url.isNotBlank())

            return ImageRequest(
                url = url,
                target = target,
                placeholderRes = placeholderRes,
                overrideWidth = overrideWidth,
                overrideHeight = overrideHeight
            )
        }
    }
}