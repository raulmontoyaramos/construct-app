package com.example.constructapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.constructapp.R
import com.example.constructapp.data.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val viewState: DashboardViewState = viewModel.viewState.collectAsState().value
    println("DashboardScreen - viewState = $viewState")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Posts") },
                navigationIcon = {
                    ConstructAppLogo(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.topbar_logo_size))
                            .padding(start = dimensionResource(R.dimen.topbar_logo_padding_start))
                    )
                },
                actions = {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(dimensionResource(R.dimen.topbar_profile_image_size)),
                        painter = rememberAsyncImagePainter(viewState.userPicUrl), //Ésto es de la librería de Coil
                        contentDescription = "User profile picture",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = viewModel::onSignOutClicked) {
                        Icon(Icons.Filled.ExitToApp, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onCreatePostClicked,
                modifier = Modifier.padding(bottom = 56.dp) // Añadido padding para elevar el FAB sobre la BottomAppBar
            ) {
                Icon(Icons.Rounded.Create, "Create new post")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Añadir espacio debajo de la TopAppBar
            when (viewState.dashboardState) {
                DashboardState.Loading ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(64.dp))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Loading posts..")
                    }

                DashboardState.Success ->
                    DashboardScreenContent(
                        viewState = viewState,
                        onTabPressed = { viewModel.onTabPressed(it) },
                        onPostClick = { viewModel.onPostClicked(it) }
                    )

                DashboardState.Empty ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No posts available yet..")
                    }

                is DashboardState.Error ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = viewState.dashboardState.errorMessage)
                    }
            }
        }
    }
}

@Composable
fun DashboardScreenContent(
    viewState: DashboardViewState,
    onTabPressed: (DashboardTab) -> Unit,
    onPostClick: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                TabRow(selectedTabIndex = viewState.selectedTab.ordinal) {
                    DashboardTab.entries.forEach { dashboardTab: DashboardTab ->
                        Tab(text = { Text(dashboardTab.text()) },
                            selected = dashboardTab == viewState.selectedTab,
                            onClick = { onTabPressed(dashboardTab) },
                            icon = {
                                Icon(
                                    imageVector = dashboardTab.icon(),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (viewState.selectedTab) {
                DashboardTab.POSTS ->
                    PostsList(
                        posts = viewState.posts,
                        onPostClick = onPostClick
                    )

                DashboardTab.MESSAGES ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Messages list")
                    }
            }
        }
    }
}

private fun DashboardTab.text() = when (this) {
    DashboardTab.POSTS -> "Posts"
    DashboardTab.MESSAGES -> "Messages"
}

private fun DashboardTab.icon() = when (this) {
    DashboardTab.POSTS -> Icons.Default.Home
    DashboardTab.MESSAGES -> Icons.Default.Email
}

@Composable
fun PostsList(
    posts: Map<String, Post>,
    onPostClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.email_list_only_horizontal_padding)),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.email_list_item_vertical_spacing)
        )
    ) {
        items(posts.toList(), key = { (postId, _) -> postId }) { (postId, post) ->
            PostsListItem(
                post = post,
                isSelected = false,
                onCardClick = { onPostClick(postId) }
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
                post = post,
                modifier = modifier.fillMaxWidth()
            )
            if (post.description.isNotEmpty()) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.email_list_item_header_subject_spacing),
                        bottom = dimensionResource(R.dimen.email_list_item_subject_body_spacing)
                    ),
                )
            }

            if (post.description.isNotEmpty()) {
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
}

@Composable
private fun PostItemHeader(post: Post, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(24.dp),
            painter = rememberAsyncImagePainter(post.userPicUrl),
            contentDescription = "User profile picture",
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.email_header_content_padding_horizontal)),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = post.userName,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(post.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun UserProfileImage(
    imageUrl: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier.clip(CircleShape),
            painter = rememberAsyncImagePainter(imageUrl), //Ésto es de la librería de Coil
            contentDescription = description,
        )
    }
}

@Composable
fun ConstructAppLogo(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Logo de ConstructApp",
        colorFilter = ColorFilter.tint(color),
        modifier = modifier
    )
}
