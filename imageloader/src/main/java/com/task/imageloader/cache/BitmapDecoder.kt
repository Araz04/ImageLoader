package com.task.imageloader.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory

internal object BitmapDecoder {

    fun decode(
        bytes: ByteArray,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            options
        )

        options.inSampleSize =
            calculateInSampleSize(
                options,
                reqWidth,
                reqHeight
            )

        options.inJustDecodeBounds = false

        options.inPreferredConfig =
            Bitmap.Config.RGB_565

        return BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            options
        )
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {

        val height = options.outHeight
        val width = options.outWidth

        var sampleSize = 1

        while (
            height / sampleSize > reqHeight &&
            width / sampleSize > reqWidth
        ) {
            sampleSize *= 2
        }

        return sampleSize
    }
}