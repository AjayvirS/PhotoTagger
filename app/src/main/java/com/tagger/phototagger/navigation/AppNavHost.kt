package com.tagger.phototagger.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tagger.phototagger.ui.screens.artspace.ArtSpaceLayout

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "artwork") {
        composable("artwork") {
            ArtSpaceLayout()
        }
        DemoRegistry.demos.forEach { demo ->
            composable(demo.route) { demo.content() }
        }
    }
}
