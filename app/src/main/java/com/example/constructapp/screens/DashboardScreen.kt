package com.example.constructapp.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.constructapp.R
import com.example.constructapp.presentation.DashboardFetchState
import com.example.constructapp.presentation.DashboardTab
import com.example.constructapp.presentation.DashboardViewModel
import com.example.constructapp.presentation.DashboardViewState
import com.example.constructapp.presentation.models.Comment
import com.example.constructapp.presentation.models.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                title = { Text(text = "ConstructApp") },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.topbar_logo_size))
                            .padding(start = dimensionResource(R.dimen.topbar_logo_padding_start))
                    )
                },
                actions = {
                    Image(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(dimensionResource(R.dimen.topbar_profile_image_size))
                            .clickable {
                                viewModel.fetchPosts()
                                viewModel.fetchComments()
                            },
                        painter = rememberAsyncImagePainter(viewState.userPicUrl),
                        contentDescription = "Profile picture",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = viewModel::onSignOutClicked) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                TabRow(selectedTabIndex = viewState.selectedTab.ordinal) {
                    DashboardTab.entries.forEach { dashboardTab: DashboardTab ->
                        Tab(text = { Text(dashboardTab.text()) },
                            selected = dashboardTab == viewState.selectedTab,
                            onClick = { viewModel.onTabPressed(dashboardTab) },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onCreatePostClicked
            ) {
                Icon(Icons.Rounded.Create, "Create new post")
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
                    DashboardPostsContent(
                        posts = viewState.posts,
                        dashboardPostsState = viewState.dashboardPostsState,
                        isRefreshingPosts = viewState.isRefreshingPosts,
                        onRefresh = viewModel::fetchPosts,
                        onPostClick = viewModel::onPostClicked
                    )

                DashboardTab.MESSAGES ->
                    DashboardCommentsContent(
                        comments = viewState.comments,
                        dashboardCommentsState = viewState.dashboardCommentsState,
                        isRefreshingComments = viewState.isRefreshingComments,
                        onRefresh = viewModel::fetchComments,
                        onCommentClicked = viewModel::onPostClicked
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCommentsContent(
    comments: Map<String, Comment>,
    dashboardCommentsState: DashboardFetchState,
    isRefreshingComments: Boolean,
    onRefresh: () -> Unit,
    onCommentClicked: (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshingComments,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (comments.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.email_list_only_horizontal_padding)),
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.email_list_item_vertical_spacing)
                )
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "My latest comments in posts:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click to see the full conversation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                items(comments.toList(), key = { (commentId, _) -> commentId }) { (_, comment) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCommentClicked(comment.postId) }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            Text(
                                text = comment.postTitle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        CommentsListItem(
                            comment = comment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        )
                    }
                }
            }
        }

        when (dashboardCommentsState) {
            DashboardFetchState.Loading ->
                if (comments.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Loading posts..")
                    }
                }

            DashboardFetchState.Success ->
                if (comments.isEmpty()) {
                    Text(
                        text = "You haven´t participated on any Post conversations",
                        modifier = Modifier.padding(16.dp)
                    )
                }


            is DashboardFetchState.Error ->
                if (comments.isEmpty()) {
                    Text(
                        text = dashboardCommentsState.errorMessage,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    val context = LocalContext.current
                    LaunchedEffect(key1 = Unit) {
                        Toast.makeText(
                            context,
                            dashboardCommentsState.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardPostsContent(
    posts: Map<String, Post>,
    dashboardPostsState: DashboardFetchState,
    isRefreshingPosts: Boolean,
    onRefresh: () -> Unit,
    onPostClick: (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshingPosts,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (posts.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.email_list_only_horizontal_padding)),
                contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.email_list_item_vertical_spacing)
                )
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Latest posts available:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click to see the full conversation",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                items(posts.toList(), key = { (postId, _) -> postId }) { (postId, post) ->
                    PostsListItem(
                        post = post,
                        enabled = true,
                        onCardClick = { onPostClick(postId) }
                    )
                }
            }
        }

        when (dashboardPostsState) {
            DashboardFetchState.Loading ->
                if (posts.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Loading posts..")
                    }
                }

            DashboardFetchState.Success ->
                if (posts.isEmpty()) {
                    Text(
                        text = "No posts available yet..  go and create the first one!",
                        modifier = Modifier.padding(16.dp)
                    )
                }


            is DashboardFetchState.Error ->
                if (posts.isEmpty()) {
                    Text(
                        text = dashboardPostsState.errorMessage,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    val context = LocalContext.current
                    LaunchedEffect(key1 = Unit) {
                        Toast.makeText(
                            context,
                            dashboardPostsState.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}

private fun DashboardTab.text() = when (this) {
    DashboardTab.POSTS -> "Posts"
    DashboardTab.MESSAGES -> "Your Comments"
}

private fun DashboardTab.icon() = when (this) {
    DashboardTab.POSTS -> Icons.Default.Home
    DashboardTab.MESSAGES -> Icons.Default.Email
}

@Composable
fun PostsListItem(
    post: Post,
    enabled: Boolean,
    onCardClick: () -> Unit = {},
    elevation: Dp = 0.dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                shape = RoundedCornerShape(8.dp),
                elevation = elevation
            ),
        enabled = enabled,
        onClick = onCardClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.email_list_item_inner_padding))
        ) {
            PostItemHeader(
                post = post,
                modifier = Modifier.fillMaxWidth()
            )
            if (post.title.isNotEmpty()) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.email_list_item_header_subject_spacing),
                        bottom = dimensionResource(R.dimen.email_list_item_subject_body_spacing)
                    ),
                    overflow = TextOverflow.Ellipsis
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

fun formatPostForSharing(post: Post): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm", Locale.getDefault())
    val formattedDateTime = Instant.ofEpochSecond(post.createdAt)
        .atZone(ZoneId.systemDefault())
        .format(formatter)

    return """
        Post by ${post.userName}
        Date: $formattedDateTime
        Title: ${post.title}
        Description: ${post.description}
    """.trimIndent()
}

@Composable
fun PostItemHeader(post: Post, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm", Locale.getDefault())
    val formattedDateTime = Instant.ofEpochSecond(post.createdAt)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
    val context = LocalContext.current
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
                text = formattedDateTime,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        IconButton(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, formatPostForSharing(post))
                }
                val chooserIntent = Intent.createChooser(shareIntent, null)
                context.startActivity(chooserIntent)
            }
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
