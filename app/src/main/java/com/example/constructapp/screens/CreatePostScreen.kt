package com.example.constructapp.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.constructapp.R

@Composable
fun CreatePostScreen(
    viewModel: CreatePostViewModel
) {
    val viewState = viewModel.viewState.collectAsState().value
    println("CreatePostScreen - viewState = $viewState")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CreatePostTopBar(onClick = viewModel::onBackButtonClicked, viewState = viewState)
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

@Composable
fun CreatePostTopBar(onClick: () -> Unit, viewState: CreatePostViewState, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.detail_topbar_back_button_padding_horizontal))
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = ""
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "New Post",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f))
        UserProfileImage(
            imageUrl = viewState.userImageUrl,
            description = "User profile picture",
            modifier = Modifier
                .size(dimensionResource(R.dimen.topbar_profile_image_size))
                .padding(end = 8.dp)
        )
    }
}

//@Composable
//fun AddImage() {
//
//    val imageUri = rememberSaveable { mutableStateOf("") }
//    val painter = rememberImagePainter(
//        if (imageUri.value.isEmpty())
//            R.drawable.empty_mage_grey
//        else
//            imageUri.value
//    )
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { imageUri.value = it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "Add an image to your post: ")
//            Icon(
//                imageVector = Icons.Default.AddBox,
//                contentDescription = "Add File",
//                modifier = Modifier.clickable { launcher.launch("image/*") } // Acción para añadir archivo
//            )
//        }
//        Card(
//            shape = CircleShape,
//            modifier = Modifier
//                .padding(8.dp)
//                .size(100.dp)
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                Image(
//                    painter = painter,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clickable { launcher.launch("image/*") },
//                    contentScale = ContentScale.Crop
//                )
//            }
//        }
//    }
//}