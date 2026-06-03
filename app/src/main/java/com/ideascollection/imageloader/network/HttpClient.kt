package com.ideascollection.imageloader.network

interface HttpClient {
    suspend fun get(url: String): ByteArray
}