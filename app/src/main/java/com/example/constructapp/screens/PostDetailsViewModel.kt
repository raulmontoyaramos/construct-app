package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.data.Comment
import com.example.constructapp.data.Post
import com.example.constructapp.data.Repository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant

class PostDetailsViewModel(
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController,
    private val postId: String,
    private val currentUser: FirebaseUser,
    private val repository: Repository
) : ViewModel() {

    val viewState = MutableStateFlow(
        PostDetailsViewState(
            post = null,
            commentsState = PostCommentsState.Loading,
            comments = emptyMap(),
            newComment = "",
            postNewCommentState = PostNewCommentState.Writing
        )
    )

    init {
        viewModelScope.launch {
            val post: Post? = withContext(Dispatchers.IO) { repository.getPostById(postId) }
            if (post == null) {
                navController.navigateUp()
                return@launch
            }
            viewState.update { it.copy(post = post) }
            fetchComments()
        }
    }

    private suspend fun fetchComments() {
        try {
            val commentsMap = withContext(Dispatchers.IO) {
                val commentsCollection: CollectionReference =
                    firebaseFirestore.collection("Posts").document(postId)
                        .collection("Messages") //Aquí no lo cambio pq en Firebase se llama Messages y no se cambiarlo todavía
                commentsCollection.get().await().associate { document: QueryDocumentSnapshot ->
                    val commentData = document.toObject(Comment::class.java)
                    println("PostDetailsViewModel - get - comment = $commentData")
                    document.id to commentData
                }
            }
            println("PostDetailsViewModel - commentesMap = $commentsMap")
            if (commentsMap.isEmpty()) {
                viewState.update { it.copy(commentsState = PostCommentsState.Empty) }
            } else {
                viewState.update {
                    it.copy(
                        commentsState = PostCommentsState.Success,
                        comments = commentsMap
                    )
                }
            }
        } catch (exception: Exception) {
            viewState.update {
                it.copy(
                    commentsState = PostCommentsState.Error(
                        exception.message ?: "Oops, error loading Posts.."
                    )
                )
            }
        } finally {
            println("PostDetailsViewModel - commentsState = ${viewState.value.commentsState}")
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
                val result: DocumentReference = withContext(Dispatchers.IO) {
                    val commentsCollection = firebaseFirestore.collection("Posts").document(postId)
                        .collection("Messages")
                    commentsCollection.add(
                        Comment(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            userPicUrl = currentUser.photoUrl.toString(),
                            body = viewState.value.newComment,
                            commentTimeStamp = Instant.now().epochSecond
                        )
                    ).await()
                }
                println("PostDetailsViewModel - result = $result")
                viewState.update { it.copy(postNewCommentState = PostNewCommentState.Success) }
                fetchComments()
                viewState.update { it.copy(newComment = "") }
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
}

data class PostDetailsViewState(
    val post: Post?,
    val commentsState: PostCommentsState,
    val comments: Map<String, Comment>,
    val newComment: String,
    val postNewCommentState: PostNewCommentState
)

sealed class PostCommentsState {
    data object Loading : PostCommentsState()
    data object Success : PostCommentsState()
    data object Empty : PostCommentsState()
    data class Error(val errorMessage: String) : PostCommentsState()
}

sealed class PostNewCommentState {
    data object Writing : PostNewCommentState()
    data object Sending : PostNewCommentState()
    data object Success : PostNewCommentState()
    data class Error(val errorMessage: String) : PostNewCommentState()
}
