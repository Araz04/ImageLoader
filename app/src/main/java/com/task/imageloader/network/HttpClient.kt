package com.task.imageloader.network

interface HttpClient {
    suspend fun get(url: String): ByteArray
}