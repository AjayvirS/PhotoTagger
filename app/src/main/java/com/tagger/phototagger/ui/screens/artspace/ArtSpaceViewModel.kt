package com.example.kotlintutorials.ui.screens.artspace

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintutorials.ui.screens.artspace.state.ArtSpaceState
import com.tagger.phototagger.data.ImageRepository
import com.tagger.phototagger.data.util.deriveTitleFromUri
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


    fun onSaveRequested(imagePath: String) {
        viewModelScope.launch {
            val annotateNow = uiState.value.annotateNow

            if (annotateNow) {
                _uiState.update { it.copy(generatedTitle = "Generating title…") }
            }

            try {
                val title = if (annotateNow) {
                    val t = imgRepo.generateAiTitle(imagePath)
                    if (t.isBlank()) deriveTitleFromUri(imagePath) else t
                } else {
                    deriveTitleFromPath(imagePath)
                }

                val rowId = imgRepo.saveImage(uri = imagePath, title = title)

                if (rowId > 0L) {
                    _uiState.update { it.copy(generatedTitle = title, isSaved = true) }
                    updateStateForCurrentIndex()
                } else {
                    _uiState.update { it.copy(generatedTitle = "Failed to save") }
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

            _uiState.update { it.copy(
                imagePath = savedRecord?.imageSource ?: currentArt.imagePath,
                imageSource = currentArt.imagePath,
                generatedTitle = savedRecord?.title ?: currentArt.title,
                isSaved = savedRecord != null,
                id = savedRecord?.id
            )}
        }
    }

}