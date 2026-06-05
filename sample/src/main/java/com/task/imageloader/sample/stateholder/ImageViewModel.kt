package com.task.imageloader.sample.stateholder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.task.imageloader.core.ImageLoader
import com.task.imageloader.sample.model.ImageItem
import com.task.imageloader.sample.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImageUiState(
    val images: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cacheClearedEvent: Boolean = false
)

class ImageViewModel(
    application: Application,
    private val repository: ImageRepository,
    private val imageLoader: ImageLoader
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ImageUiState())
    val uiState: StateFlow<ImageUiState> = _uiState.asStateFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val images = repository.fetchImages()
                _uiState.update { it.copy(images = images, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun invalidateCache() {
        imageLoader.clearCache()
        _uiState.update { state ->
            state.copy(
                cacheClearedEvent = true,
                images = state.images.toList()
            )
        }
    }

    fun onCacheClearedEventConsumed() {
        _uiState.update { it.copy(cacheClearedEvent = false) }
    }

    fun invalidateUrl(url: String) {
        imageLoader.invalidate(url)
    }
}
