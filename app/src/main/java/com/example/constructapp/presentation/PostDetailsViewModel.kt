package com.example.constructapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.constructapp.data.NetworkService
import com.example.constructapp.data.PostsRepository
import com.example.constructapp.presentation.models.Comment
import com.example.constructapp.presentation.models.Post
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class PostDetailsViewModel(
    private val networkService: NetworkService,
    private val navController: NavHostController,
    private val postId: String,
    private val currentUser: FirebaseUser,
    private val postsRepository: PostsRepository
) : ViewModel() {

    val viewState = MutableStateFlow(
        PostDetailsViewState(
            post = null,
            commentsState = PostCommentsState.Loading,
            comments = emptyMap(),
            newComment = "",
            postNewCommentState = PostNewCommentState.Writing,
            isRefreshing = false
        )
    )

    init {
        viewModelScope.launch {
            val post: Post? = withContext(Dispatchers.IO) { postsRepository.getPostById(postId) }
            if (post == null) {
                fetchPost()
            }
            viewState.update { it.copy(post = post) }
            fetchComments()
        }
    }

    fun fetchPost() {
        viewModelScope.launch {
//            viewState.update {
//                it.copy(
//                    commentsState = PostCommentsState.Loading,
//                    isRefreshing = true
//                )
//            }
            try {
                val post = withContext(Dispatchers.IO) {
                    networkService.getPost(postId)
                }
                println("PostDetailsViewModel - post success = $post")
                viewState.update { it.copy(post = post) }
            } catch (exception: Exception) {
                println("PostDetailsViewModel - post error = ${exception.message}")
                navController.navigateUp()
                return@launch
            } finally {
                println("PostDetailsViewModel - post = ${viewState.value.post}")
            }
        }
    }

    fun fetchComments() {
        viewModelScope.launch {
            viewState.update {
                it.copy(
                    commentsState = PostCommentsState.Loading,
                    isRefreshing = true
                )
            }
            try {
                val commentsMap = withContext(Dispatchers.IO) {
                    networkService.getComments(postId)
                }
                println("PostDetailsViewModel - commentesMap = $commentsMap")
                viewState.update {
                    it.copy(
                        commentsState = PostCommentsState.Success,
                        comments = commentsMap,
                        isRefreshing = false
                    )
                }
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        commentsState = PostCommentsState.Error(
                            exception.message ?: "Oops, error loading Posts.."
                        ),
                        isRefreshing = false
                    )
                }
            } finally {
                println("PostDetailsViewModel - commentsState = ${viewState.value.commentsState}")
            }
        }
    }

    fun onBackButtonClicked() = navController.navigateUp()

    fun onCommentTextChanged(newText: String) {
        viewState.update { it.copy(newComment = newText) }
    }

    fun onReplyButtonClicked() {
        println("PostDetailsViewModel - onReplyButtonClicked")
        viewModelScope.launch {
            try {
                viewState.update { it.copy(postNewCommentState = PostNewCommentState.Sending) }
                withContext(Dispatchers.IO) {
                    networkService.addComment(
                        postId,
                        Comment(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            userPicUrl = currentUser.photoUrl.toString(),
                            body = viewState.value.newComment,
                            createdAt = Instant.now().epochSecond,
                            postId = postId,
                            postTitle = viewState.value.post?.title.orEmpty()
                        )
                    )
                }
                println("PostDetailsViewModel - onReplyButtonClicked success")
                viewState.update {
                    it.copy(
                        postNewCommentState = PostNewCommentState.Success,
                        newComment = ""
                    )
                }
                fetchComments()
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        postNewCommentState = PostNewCommentState.Error(
                            exception.message ?: "Oops, error creating the comment"
                        )
                    )
                }
            } finally {
                println("PostDetailsViewModel - postNewCommentState=${viewState.value.postNewCommentState}")
            }
        }
    }

    fun onDeleteCommentClicked(commentId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    networkService.removeComment(postId, commentId)
                }
                fetchComments()
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        commentsState = PostCommentsState.Error(
                            exception.message ?: "Oops, error deleting the comment"
                        )
                    )
                }
            }
        }
    }

}

data class PostDetailsViewState(
    val post: Post?,
    val commentsState: PostCommentsState,
    val comments: Map<String, Comment>,
    val newComment: String,
    val postNewCommentState: PostNewCommentState,
    val isRefreshing: Boolean
)

sealed class PostCommentsState {
    data object Loading : PostCommentsState()
    data object Success : PostCommentsState()
    data class Error(val errorMessage: String) : PostCommentsState()
}

sealed class PostNewCommentState {
    data object Writing : PostNewCommentState()
    data object Sending : PostNewCommentState()
    data object Success : PostNewCommentState()
    data class Error(val errorMessage: String) : PostNewCommentState()
}
