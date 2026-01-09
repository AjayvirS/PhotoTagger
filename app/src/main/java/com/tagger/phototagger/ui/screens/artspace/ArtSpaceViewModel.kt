package com.example.kotlintutorials.ui.screens.artspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintutorials.ui.screens.artspace.state.ArtSpaceState
import com.tagger.phototagger.data.ImageRepository
import com.tagger.phototagger.data.util.deriveTitleFromUri
import com.tagger.phototagger.data.util.toUriFlexible
import com.tagger.phototagger.model.Artwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtSpaceViewModel @Inject constructor(
    private val imgRepo: ImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtSpaceState())
    val uiState: StateFlow<ArtSpaceState> = _uiState

    private var currentIndex = 0
    private val artworkList: List<Artwork> = imgRepo.getSampleArtworks()

    init {
        if (artworkList.isNotEmpty()) {
            updateStateForCurrentIndex()
        }
    }


    fun onSaveRequested(imagePath: String, annotateNow: Boolean = false) {
        viewModelScope.launch {
            if (annotateNow) {
                _uiState.update { it.copy(generatedTitle = "Generating title…") }
            }

            val baseTitle = deriveTitleFromUri(imagePath.toUriFlexible())

            try {
                val title = if (annotateNow) {
                    imgRepo.generateAiTitle(imagePath).takeIf { it.isNotBlank() } ?: baseTitle
                } else {
                    baseTitle
                }

                val storedPath = imgRepo.saveImage(uri = imagePath, title = title)

                if (storedPath != null) {
                    _uiState.value = _uiState.value.copy(
                        generatedTitle = title,
                        isSaved = true,
                        imagePath = storedPath
                    )
                    updateStateForCurrentIndex()
                } else {
                    _uiState.value = _uiState.value.copy(generatedTitle = "Failed to save")
                }
            } catch (t: Throwable) {
                _uiState.update { it.copy(generatedTitle = "Error: ${t.message ?: "unexpected"}") }
            }
        }
    }

    fun onNextImage() {
        currentIndex = (currentIndex + 1) % artworkList.size
        updateStateForCurrentIndex()
    }

    fun onPreviousImage() {
        currentIndex = if (currentIndex == 0) artworkList.size - 1 else currentIndex - 1
        updateStateForCurrentIndex()
    }

    private fun updateStateForCurrentIndex() {
        val currentArt = artworkList[currentIndex]

        viewModelScope.launch {
            val savedRecord = imgRepo.getSavedRecordByUri(currentArt.imagePath)

            _uiState.update {
                it.copy(
                    imagePath = savedRecord?.imageSource ?: currentArt.imagePath,
                    imageSource = currentArt.imagePath,
                    generatedTitle = savedRecord?.title ?: currentArt.title,
                    isSaved = savedRecord != null,
                    id = savedRecord?.id
                )
            }
        }
    }

}