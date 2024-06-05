package com.example.constructapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.constructapp.R
import com.example.constructapp.data.Comment
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(viewModel: PostDetailsViewModel) {
    val viewState: PostDetailsViewState = viewModel.viewState.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Post details") },
                modifier = Modifier.zIndex(1f),
                navigationIcon = {
                    IconButton(onClick = viewModel::onBackButtonClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            PostDetailsScreenBottomBar(
                onButtonClicked = viewModel::onReplyButtonClicked,
                commentText = viewState.newComment,
                onValueChange = viewModel::onCommentTextChanged
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                viewState.post?.let {
                    PostsListItem(
                        post = it,
                        enabled = false,
                        elevation = 8.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (viewState.comments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (viewState.commentsState) {
                                PostCommentsState.Loading ->
                                    CircularProgressIndicator()

                                is PostCommentsState.Error ->
                                    Text(text = viewState.commentsState.errorMessage)

                                PostCommentsState.Success ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "No comments yet..")
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(onClick = viewModel::fetchComments) {
                                            Text(text = "Retry")
                                        }
                                    }
                            }
                        }
                    } else {
                        PullToRefreshBox(
                            isRefreshing = viewState.isRefreshing,
                            onRefresh = viewModel::fetchComments,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .padding(start = 16.dp)
                            ) {
                                items(
                                    viewState.comments.toList(),
                                    key = { (id, _) -> id }) { (id, comment) ->
                                    CommentsListItem(comment = comment)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                        when (viewState.commentsState) {
                            is PostCommentsState.Error -> {
                                val context = LocalContext.current
                                LaunchedEffect(key1 = Unit) {
                                    Toast.makeText(
                                        context,
                                        viewState.commentsState.errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            PostCommentsState.Loading,
                            PostCommentsState.Success -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsListItem(
    comment: Comment
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.email_list_item_inner_padding))
        ) {
            CommentHeader(
                comment = comment,
                modifier = Modifier.fillMaxWidth()
            )

            if (comment.body.isNotEmpty()) {
                Text(
                    text = comment.body,
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
fun CommentHeader(comment: Comment, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .clip(CircleShape)
                .size(24.dp),
            painter = rememberAsyncImagePainter(comment.userPicUrl),
            contentDescription = "User profile picture",
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.email_header_content_padding_horizontal)),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = comment.userName,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(comment.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreenBottomBar(
    onButtonClicked: () -> Unit,
    commentText: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = commentText,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(160.dp),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            if (commentText.isEmpty()) {
                Text(
                    text = "Type your Comment on this Post", style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
        },
        trailingIcon = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .size(33.dp)
                    .clickable(onClick = onButtonClicked),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    )
}
