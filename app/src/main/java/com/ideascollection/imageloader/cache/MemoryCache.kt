package com.ideascollection.imageloader.cache

import android.graphics.Bitmap

interface MemoryCache {
    fun get(url: String): Bitmap?
    fun put(url: String, bitmap: Bitmap)
    fun clear()
}