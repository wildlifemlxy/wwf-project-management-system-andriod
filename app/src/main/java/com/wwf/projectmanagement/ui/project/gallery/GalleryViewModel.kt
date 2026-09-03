package com.wwf.projectmanagement.ui.project.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wwf.projectmanagement.data.remote.GalleryMedia
import com.wwf.projectmanagement.data.remote.GalleryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Result of listing the backend gallery. */
sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data class Loaded(val items: List<GalleryMedia>) : GalleryUiState
    data class Error(val message: String) : GalleryUiState
}

/** Loads one project's gallery listing and hands out per-file download state via [repository]. */
class GalleryViewModel(application: Application, private val projectId: String) : AndroidViewModel(application) {
    val repository: GalleryRepository = GalleryRepository.get()

    private val _state = MutableStateFlow<GalleryUiState>(GalleryUiState.Loading)
    val state: StateFlow<GalleryUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.value = GalleryUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                GalleryUiState.Loaded(repository.list(projectId))
            } catch (e: Exception) {
                GalleryUiState.Error(e.message ?: e.javaClass.simpleName)
            }
        }
    }
}

/** Builds a [GalleryViewModel] for [projectId]; use with `viewModel(key = projectId, factory = ...)`. */
class GalleryViewModelFactory(private val application: Application, private val projectId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GalleryViewModel(application, projectId) as T
}
