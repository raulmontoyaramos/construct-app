package com.example.constructapp

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.constructapp.data.Post

@Composable
fun PostsList(
    dashboardUiState: DashboardUiState,
    onPostCardPressed: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts = dashboardUiState.postList

    LazyColumn(
        modifier = modifier,
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.email_list_item_vertical_spacing)
        )
    ) {
        item {
            HomeTopBar()
        }
        items(posts) { post ->
            PostsListItem(
                post = post,
                isSelected = false,
                onCardClick = {
                    onPostCardPressed(post)
                }
            )
        }
    }
}

@Composable
fun PostsListItem(
    post: Post,
    onCardClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        onClick = onCardClick
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.email_list_item_inner_padding))
        ) {
            PostItemHeader(
                post = post
            )
            Text(
                text = post.userName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    top = dimensionResource(R.dimen.email_list_item_header_subject_spacing),
                    bottom = dimensionResource(R.dimen.email_list_item_subject_body_spacing)
                ),
            )
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun PostItemHeader(post: Post) {
    Row {
//        UserProfileImage(
//            drawableResource = post.sender.avatar,
//            description = stringResource(post.sender.firstName) + " "
//                    + stringResource(post.sender.lastName),
//            modifier = Modifier.size(dimensionResource(R.dimen.email_header_profile_size))
//        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    horizontal = dimensionResource(R.dimen.email_header_content_padding_horizontal),
                    vertical = dimensionResource(R.dimen.email_header_content_padding_vertical)
                ),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = post.userName,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.topbar_padding_vertical))
    ) {
        ConstructAppLogo(
            modifier = Modifier
                .size(dimensionResource(R.dimen.topbar_logo_size))
                .padding(start = dimensionResource(R.dimen.topbar_logo_padding_start))
        )
//        UserProfileImage(
//            drawableResource = LocalAccountsDataProvider.defaultUser.avatar,
//            description = "Profile",
//            modifier = Modifier
//                .padding(end = dimensionResource(R.dimen.topbar_profile_image_padding_end))
//                .size(dimensionResource(R.dimen.topbar_profile_image_size))
//        )
    }
}

@Composable
fun ConstructAppLogo(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color),
        modifier = modifier
    )
}

@Composable
fun UserProfileImage(
    @DrawableRes drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier.clip(CircleShape),
            painter = painterResource(drawableResource),
            contentDescription = description,
        )
    }
}