package com.tagger.phototagger.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable

data class DemoDestination(
    val route: String,
    @StringRes val titleRes: Int,
    val content: @Composable () -> Unit
)
