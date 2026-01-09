package com.example.kotlintutorials.navigation

import CompletedTasks
import com.example.kotlintutorials.R
import com.example.kotlintutorials.ui.screens.artspace.ArtSpaceLayout
import com.example.kotlintutorials.ui.screens.dice.DiceWithButtonAndImage
import com.example.kotlintutorials.ui.screens.greetings.GreetingText
import com.example.kotlintutorials.ui.screens.lemonade.LemonMaker
import com.example.kotlintutorials.ui.screens.quadrant.ComposeArticle
import com.example.kotlintutorials.ui.screens.quadrant.ComposeQuadrant
import com.example.kotlintutorials.ui.screens.tiptime.TipTimeLayout

object DemoRegistry {

    val demos: List<DemoDestination> = listOf(
        DemoDestination("artwork", R.string.title_artwork) { ArtSpaceLayout() }
    )
}
