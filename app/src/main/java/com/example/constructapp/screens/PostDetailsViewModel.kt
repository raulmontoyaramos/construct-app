package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
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
    private val navController: NavHostController,
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
                        .collection("Messages")
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

    fun onDeleteCommentClicked(commentId: String) { //Tendría que agregar un campo commentId a la colección Messages
        viewModelScope.launch {    //o tendríamos que hacer un repositorio como para el id del Post?
            try {
                withContext(Dispatchers.IO) {
                    val comment = firebaseFirestore.collection("Posts").document(postId)
                        .collection("Messages").document(commentId)
                    comment.delete().await()
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
    val postNewCommentState: PostNewCommentState
)

sealed class PostCommentsState {
    data object Loading : PostCommentsState()
    data object Success : PostCommentsState()
    data object Empty : PostCommentsState()
    data class Error(val errorMessage: String) : PostCommentsState()
}

sealed class PostNewCommentState {
    data object Writing : PostNewCommentState() //pongo Unit pq es lo que ya hay en pantalla?
    data object Sending : PostNewCommentState() //pongo el círculo de carga?
    data object Success : PostNewCommentState() //pongo un toast con mensaje de éxito?
    data class Error(val errorMessage: String) :
        PostNewCommentState() //pongo un toast con mensaje de error?
}
