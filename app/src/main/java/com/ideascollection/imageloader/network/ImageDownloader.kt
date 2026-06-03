package com.ideascollection.imageloader.network

interface ImageDownloader {
    suspend fun download(url: String): ByteArray
}