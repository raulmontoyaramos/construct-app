package com.example.constructapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.constructapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    viewModel: PostDetailsViewModel
) {
    val viewState: PostDetailsViewState = viewModel.viewState.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Post details") },
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackButtonClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
            viewState.post?.let {
                PostsListItem(
                    post = it,
                    enabled = false
                ) {}

                Spacer(modifier = Modifier.height(12.dp))

                PostDetailsScreenBottomBar()
            }
        }

    }
}

@Composable
private fun PostDetailsScreenBottomBar() {
    Box {
        ActionButton(
            text = "Reply",
            onButtonClicked = { println("DetailsScreenButtonBar - onButtonClicked") }
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    onButtonClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Button(
            onClick = { onButtonClicked(text) }, //Aquí estará el onReplyClicked de DetailsViewModel
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.detail_action_button_padding_vertical)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
