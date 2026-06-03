package com.ideascollection.imageloader.internal

import com.ideascollection.imageloader.cache.DiskCache
import com.ideascollection.imageloader.cache.MemoryCache
import com.ideascollection.imageloader.core.DispatcherProvider
import com.ideascollection.imageloader.network.ImageDownloader

internal class ImageLoaderImpl(
    private val memoryCache: MemoryCache,
    private val diskCache: DiskCache,
    private val downloader: ImageDownloader,
    private val dispatchers: DispatcherProvider
)