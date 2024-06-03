package com.example.constructapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.constructapp.R
import com.example.constructapp.data.Comment
import com.example.constructapp.data.Post
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
                    PostsInDetails(
                        post = it,
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    when (viewState.commentsState) {
                        PostCommentsState.Empty -> Box(Modifier.weight(1f)) {
                            Text(text = "No comments yet..")
                        }

                        is PostCommentsState.Error -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = viewState.commentsState.errorMessage)
                        }

                        PostCommentsState.Loading -> Box(Modifier.weight(1f)) {
                            CircularProgressIndicator()
                        }

                        PostCommentsState.Success -> LazyColumn(Modifier.weight(1f)) {
                            items(
                                viewState.comments.toList(),
                                key = { (id, _) -> id }) { (id, comment) ->
                                CommentsListItem(comment = comment)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostsInDetails(
    post: Post,
    enabled: Boolean,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().zIndex(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
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
fun CommentsListItem(
    comment: Comment,
    enabled: Boolean = false,
    onCardClick: () -> Unit = {}
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        elevation = CardDefaults.cardElevation(8.dp)
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
                text = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(comment.commentTimeStamp)),
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
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
