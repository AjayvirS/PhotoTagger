package com.example.kotlintutorials.ui.screens.artspace

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlintutorials.ui.screens.artspace.state.ArtSpaceState
import com.tagger.phototagger.data.ImageRepository
import com.tagger.phototagger.data.util.deriveTitleFromUri
import com.tagger.phototagger.data.util.toUriFlexible
import com.tagger.phototagger.model.Artwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArtSpaceViewModel @Inject constructor(
    private val imgRepo: ImageRepository
) : ViewModel() {

    private val artworkList: List<Artwork> = imgRepo.getSampleArtworks()

    private val indexFlow = MutableStateFlow(0)
    private val selectedUriFlow = MutableStateFlow<String?>(null)
    private val processingStatus = MutableStateFlow<String?>(null)
    private val saveRequests = MutableSharedFlow<SaveRequest>(extraBufferCapacity = 1)

    private val selectionFlow: Flow<Selection?> =
        combine(indexFlow, selectedUriFlow) { idx, customUri ->
            if (customUri != null) {
                Selection(customUri, customUri, "", customUri)
            } else {
                artworkList.getOrNull(idx)?.let { art ->
                    Selection(art.imagePath, art.imagePath, art.title, art.imagePath)
                }
            }
        }.distinctUntilChanged()

    val uiState: StateFlow<ArtSpaceState> = combine(
        selectionFlow,
        processingStatus
    ) { selection, status ->
        selection to status
    }.flatMapLatest { (selection, status) ->
        if (selection == null) {
            flowOf(ArtSpaceState(generatedTitle = "No artworks available"))
        } else {
            // REACTIVE: Whenever selection changes, we observe the DB for that specific key.
            // When the DB updates (via handleSave), this block automatically re-runs.
            imgRepo.observeSavedRecordByUri(selection.sourceKey).map { savedRecord ->
                ArtSpaceState(
                    imageSource = selection.imageSource,
                    imagePath = savedRecord?.imagePath ?: selection.imagePath,
                    generatedTitle = status ?: savedRecord?.title ?: selection.title,
                    isSaved = savedRecord != null,
                    id = savedRecord?.id
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ArtSpaceState(
            generatedTitle = if (artworkList.isEmpty()) "No artworks available" else "Loading..."
        )
    )

    init {
        viewModelScope.launch {
            saveRequests.collectLatest { handleSave(it) }
        }
    }


    fun onNextImage() {
        if (artworkList.isEmpty()) return
        resetState()
        indexFlow.update { (it + 1) % artworkList.size }
    }

    fun onPreviousImage() {
        if (artworkList.isEmpty()) return
        resetState()
        indexFlow.update { idx -> if (idx == 0) artworkList.size - 1 else idx - 1 }
    }

    fun selectImage(uri: Uri) {
        resetState()
        selectedUriFlow.value = uri.toString()
    }

    fun onSaveRequested(imagePath: String, annotateNow: Boolean = false) {
        saveRequests.tryEmit(SaveRequest(imagePath = imagePath, annotateNow = annotateNow))
    }

    private fun resetState() {
        processingStatus.value = null
        selectedUriFlow.value = null
    }


    private suspend fun handleSave(req: SaveRequest) {
        try {
            if (req.annotateNow) processingStatus.value = "Generating title…"

            val baseTitle = deriveTitleFromUri(req.imagePath.toUriFlexible())
            val title = if (req.annotateNow) {
                imgRepo.generateAiTitle(req.imagePath).takeIf { it.isNotBlank() } ?: baseTitle
            } else baseTitle

            val storedPath = imgRepo.saveImage(uri = req.imagePath, title = title)

            if (storedPath == null) {
                processingStatus.value = "Failed to save"
            } else {
                selectedUriFlow.value = req.imagePath
                processingStatus.value = null
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            processingStatus.value = "Error: ${e.message ?: "unexpected"}"
        }
    }

    private data class SaveRequest(val imagePath: String, val annotateNow: Boolean)
    private data class Selection(val imageSource: String, val imagePath: String, val title: String, val sourceKey: String)
}


