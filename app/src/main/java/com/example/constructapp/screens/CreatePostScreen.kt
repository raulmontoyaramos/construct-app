package com.example.constructapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.example.constructapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel
) {
    val viewState = viewModel.viewState.collectAsState().value
    println("CreatePostScreen - viewState = $viewState")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "New post") },
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackButtonClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(dimensionResource(R.dimen.topbar_profile_image_size)),
                        painter = rememberAsyncImagePainter(viewState.userImageUrl), //Ésto es de la librería de Coil
                        contentDescription = "User profile picture",
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "User: ${viewState.userName}")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewState.title,
                    onValueChange = { newText -> viewModel.onTitleUpdated(newText) },
                    label = { Text(text = "Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = viewState.description,
                    onValueChange = viewModel::onDescriptionUpdated,
                    label = { Text(text = "Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1F))
                Button(onClick = viewModel::onCreatePostClicked) {
                    Text(text = "Create")
                }
            }
        }
        when (viewState.createPostState) {
            CreatePostState.Creating ->
                Dialog(
                    onDismissRequest = { Unit },
                    DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                    ) {
                        CircularProgressIndicator()
                    }
                }

            is CreatePostState.Error ->
                AlertDialog(
                    onDismissRequest = viewModel::onDismissRequest,
                    confirmButton = {
                        TextButton(onClick = viewModel::onCreatePostClicked) {
                            Text(text = "Retry")
                        }
                    },
                    title = { Text(text = "New Post") },
                    text = { Text(text = viewState.createPostState.errorMessage) }
                )

            CreatePostState.Filling -> Unit
            CreatePostState.Success ->
                AlertDialog(
                    onDismissRequest = { viewModel.onDismissRequest() },
                    confirmButton = {
                        TextButton(onClick = viewModel::onOkClicked) {
                            Text(text = "Ok")
                        }
                    },
                    title = { Text(text = "New Post") },
                    text = { Text(text = "Post has been created successfully!") }
                )
        }
    }
}
