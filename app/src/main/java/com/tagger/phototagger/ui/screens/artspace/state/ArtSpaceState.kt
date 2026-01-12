package com.tagger.phototagger.ui.screens.artspace.state

data class ArtSpaceState(val imagePath: String = "",
                         val id: Int? = null,
                         val imageSource: String = "",
                         val generatedTitle: String = "",
                         val isSaved: Boolean = false)
