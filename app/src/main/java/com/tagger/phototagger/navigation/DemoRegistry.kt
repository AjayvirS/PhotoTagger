package com.tagger.phototagger.navigation

import com.tagger.phototagger.ui.screens.artspace.ArtSpaceLayout
import com.tagger.phototagger.R


object DemoRegistry {

    val demos: List<DemoDestination> = listOf(
        DemoDestination("artwork", R.string.title_artwork) { ArtSpaceLayout() }
    )
}
