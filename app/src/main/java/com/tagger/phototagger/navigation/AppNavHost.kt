package com.example.kotlintutorials.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tagger.phototagger.navigation.DemoRegistry

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {

        }
        DemoRegistry.demos.forEach { demo ->
            composable(demo.route) { demo.content() }
        }
    }
}
