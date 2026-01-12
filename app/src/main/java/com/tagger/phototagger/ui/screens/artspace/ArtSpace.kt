    package com.tagger.phototagger.ui.screens.artspace

    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.PickVisualMediaRequest
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ArrowBack
    import androidx.compose.material.icons.automirrored.filled.ArrowForward
    import androidx.compose.material.icons.filled.Favorite
    import androidx.compose.material.icons.filled.FavoriteBorder
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.Color.Companion.LightGray
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.text.font.FontWeight.Companion.Bold
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.hilt.navigation.compose.hiltViewModel
    import coil.compose.AsyncImage

    @Composable
    fun ArtSpaceLayout(viewModel: ArtSpaceViewModel = hiltViewModel(), modifier: Modifier = Modifier) {
        val state by viewModel.uiState.collectAsState()

        val isBusy = state.generatedTitle == "Generating title…" || state.generatedTitle.startsWith("Loading")

        val pickImage = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let { viewModel.selectImage(it) }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = LightGray.copy(alpha = 0.5f))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                ArtworkWall(modifier = Modifier.weight(3f), imageUri = state.imagePath)

                ArtworkDescriptor(state.generatedTitle, modifier = Modifier.weight(0.5f))

                Button(
                    onClick = {
                        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    enabled = !isBusy
                ) {
                    Text("Pick Custom Image")
                }

                ArtworkController(
                    isSaved = state.isSaved,
                    isBusy = isBusy,
                    onPrev = { viewModel.onPreviousImage() },
                    onNext = { viewModel.onNextImage() },
                    onSave = { viewModel.onSaveRequested(state.imageSource, annotateNow = true) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun ArtworkController(
        isSaved: Boolean,
        isBusy: Boolean,
        onPrev: () -> Unit,
        onNext: () -> Unit,
        onSave: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {

                // Previous Button
                Button(
                    onClick = onPrev,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }

                Button(
                    onClick = onSave,
                    enabled = !isBusy && !isSaved,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            isBusy -> Color.LightGray
                            isSaved -> Color.Red
                            else -> Color.Gray
                        }
                    ),
                    elevation = ButtonDefaults.buttonElevation(12.dp)
                ) {
                    // If you want to get fancy, you could put a CircularProgressIndicator here
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save Image"
                    )
                }

                // Next Button
                Button(
                    onClick = onNext,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
    @Composable
    fun ArtworkDescriptor(title: String, artBy: String = "", modifier: Modifier = Modifier) {
        Row(modifier = modifier) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, fontSize = 24.sp, fontWeight = Bold)
                Text(text = artBy)
            }
        }
    }

    @Composable
    fun ArtworkWall(imageUri: String, modifier: Modifier = Modifier) {
        Surface(
            modifier = modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 20.dp
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Artwork",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(5.dp, Color.DarkGray, RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }