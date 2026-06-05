package com.task.imageloader.utils

import java.io.File

object FileUtils {
    fun saveBytes(file: File, bytes: ByteArray) {
        file.parentFile?.mkdirs()
        file.outputStream().use { it.write(bytes) }
    }

    fun readBytes(file: File): ByteArray? {
        if (!file.exists()) return null
        return file.inputStream().use { it.readBytes() }
    }
}