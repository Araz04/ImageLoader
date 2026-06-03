package com.ideascollection.imageloader.cache

import android.graphics.Bitmap

interface DiskCache {
    fun get(url: String): Bitmap?
    fun put(url: String, bitmap: Bitmap)
    fun clear()
    fun isValid(url: String): Boolean
}