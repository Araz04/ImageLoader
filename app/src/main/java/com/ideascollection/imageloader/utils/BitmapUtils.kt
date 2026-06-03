package com.ideascollection.imageloader.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun decode(bytes: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}