package com.example.constructapp.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.constructapp.R
import com.example.constructapp.DashboardUiState

@Composable
fun DetailsScreen(
    constructAppUiState: DashboardUiState,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackPressed()
    }
    Box(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            item {
                DetailsScreenTopBar(
                    onBackPressed,
                    constructAppUiState,
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = dimensionResource(R.dimen.detail_topbar_padding_bottom),
                            top = dimensionResource(R.dimen.topbar_padding_vertical)
                        )
                )

//                PostDetailsCard(
//                    post = constructAppUiState.currentSelectedPost,
//                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.detail_card_outer_padding_horizontal))
//                )
            }
        }
    }
}

@Composable
private fun DetailsScreenTopBar(
    onBackButtonClicked: () -> Unit,
    constructAppUiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackButtonClicked,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.detail_topbar_back_button_padding_horizontal))
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = dimensionResource(R.dimen.detail_subject_padding_end))
        ) {
            Text(
                text = "Current Post Subject",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}









//@Composable
//private fun PostDetailsCard(
//    post: Post,
//    modifier: Modifier = Modifier,
//) {
//    val context = LocalContext.current
//    val displayToast = { text: String ->
//        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
//    }
//    Card(
//        modifier = modifier,
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(dimensionResource(R.dimen.detail_card_inner_padding))
//        ) {
//            DetailsScreenHeader(
//                post,
//                Modifier.fillMaxWidth()
//            )
//                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.detail_content_padding_top)))
//
//            Text(
//                text = stringResource(post.body),
//                style = MaterialTheme.typography.bodyLarge,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//            )
//            DetailsScreenButtonBar(displayToast)
//        }
//    }
//}

//@Composable
//private fun DetailsScreenHeader(post: Post, modifier: Modifier = Modifier) {
//    Row(modifier = modifier) {
//        UserProfileImage(
//            drawableResource = post.sender.avatar,
//            description = stringResource(post.sender.firstName) + " "
//                    + stringResource(post.sender.lastName),
//            modifier = Modifier.size(
//                dimensionResource(R.dimen.email_header_profile_size)
//            )
//        )
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .padding(
//                    horizontal = dimensionResource(R.dimen.email_header_content_padding_horizontal),
//                    vertical = dimensionResource(R.dimen.email_header_content_padding_vertical)
//                ),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = stringResource(post.sender.firstName),
//                style = MaterialTheme.typography.labelMedium
//            )
//            Text(
//                text = stringResource(post.createdAt),
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.outline
//            )
//        }
//    }
//}

//@Composable
//private fun DetailsScreenButtonBar(
//    displayToast: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(modifier = modifier) {
//        ActionButton(
//            text = "Reply",
//            onButtonClicked = displayToast
//        )
//    }
//}

//@Composable
//private fun ActionButton(
//    text: String,
//    onButtonClicked: (String) -> Unit,
//    modifier: Modifier = Modifier,
//    containIrreversibleAction: Boolean = false,
//) {
//    Box(modifier = modifier) {
//        Button(
//            onClick = { onButtonClicked(text) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = dimensionResource(R.dimen.detail_action_button_padding_vertical)),
//            colors = ButtonDefaults.buttonColors(
//                containerColor =
//                if (containIrreversibleAction) {
//                    MaterialTheme.colorScheme.onErrorContainer
//                } else {
//                    MaterialTheme.colorScheme.primaryContainer
//                }
//            )
//        ) {
//            Text(
//                text = text,
//                color = if (containIrreversibleAction) {
//                    MaterialTheme.colorScheme.onError
//                } else {
//                    MaterialTheme.colorScheme.onSurfaceVariant
//                }
//            )
//        }
//    }
//}