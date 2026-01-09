package com.example.kotlintutorials.ui.screens.artspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintutorials.data.ImageRepository
import com.example.kotlintutorials.model.Artwork
import com.example.kotlintutorials.ui.screens.artspace.state.ArtSpaceState
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
            _uiState.update {
                it.copy(generatedTitle = "Generating AI title...")
            }

            val aiTitle = imgRepo.generateAiTitle(imagePath)
            imgRepo.saveImage(uri=imagePath, title = aiTitle)
            updateStateForCurrentIndex()
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