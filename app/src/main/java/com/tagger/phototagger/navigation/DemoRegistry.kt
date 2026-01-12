package com.tagger.phototagger.navigation

import com.example.kotlintutorials.R
import com.example.kotlintutorials.navigation.DemoDestination
import com.example.kotlintutorials.ui.screens.artspace.ArtSpaceLayout


object DemoRegistry {

    val demos: List<DemoDestination> = listOf(
        DemoDestination("artwork", R.string.title_artwork) { ArtSpaceLayout() }
    )
}
