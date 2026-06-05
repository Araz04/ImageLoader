package com.task.imageloader.sample.stateholder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.task.imageloader.core.ImageLoader
import com.task.imageloader.sample.model.ImageItem
import com.task.imageloader.sample.repository.ImageRepository
import kotlinx.coroutines.launch

class ImageViewModel(
    application: Application,
    private val imageLoader: ImageLoader,
    private val repository: ImageRepository
) : AndroidViewModel(application) {

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _cacheClearedEvent = MutableLiveData(false)
    val cacheClearedEvent: LiveData<Boolean> = _cacheClearedEvent

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _images.value = repository.fetchImages()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Failed to load images"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun invalidateCache() {
        imageLoader.clearCache()
        _cacheClearedEvent.value = true
    }

    fun onCacheClearedEventConsumed() {
        _cacheClearedEvent.value = false
    }

    fun invalidateUrl(url: String) = imageLoader.invalidate(url)
}
