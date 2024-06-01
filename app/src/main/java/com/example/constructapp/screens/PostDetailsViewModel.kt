package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.data.Message
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
            messagesState = PostMessagesState.Loading,
            messages = emptyMap(),
            newMessage = ""
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
            fetchMessages()
        }
    }

    private suspend fun fetchMessages() {
        try {
            val messagesMap = withContext(Dispatchers.IO) {
                val messagesCollection: CollectionReference =
                    firebaseFirestore.collection("Posts").document(postId)
                        .collection("Messages")
                messagesCollection.get().await().associate { document: QueryDocumentSnapshot ->
                    val messageData = document.toObject(Message::class.java)
                    println("PostDetailsViewModel - get - message = $messageData")
                    document.id to messageData
                }
            }
            println("PostDetailsViewModel - messagesMap = $messagesMap")
            if (messagesMap.isEmpty()) {
                viewState.update { it.copy(messagesState = PostMessagesState.Empty) }
            } else {
                viewState.update {
                    it.copy(
                        messagesState = PostMessagesState.Success,
                        messages = messagesMap
                    )
                }
            }
        } catch (exception: Exception) {
            viewState.update {
                it.copy(
                    messagesState = PostMessagesState.Error(
                        exception.message ?: "Oops, error loading Posts.."
                    )
                )
            }
        } finally {
            println("PostDetailsViewModel - messagesState = ${viewState.value.messagesState}")
        }
    }

    fun onBackButtonClicked() = navController.navigateUp()

    fun onReplyButtonClicked() {
        println("PostDetailsViewModel - onReplyButtonClicked")
        viewModelScope.launch {
            try {
                val result: DocumentReference = withContext(Dispatchers.IO) {
                    val messagesCollection = firebaseFirestore.collection("Posts").document(postId)
                        .collection("Messages")
                    messagesCollection.add(
                        Message(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            userPicUrl = currentUser.photoUrl.toString(),
                            message = "new messagee", // viewState.value.newMessage,
                            createdAt = Instant.now().epochSecond
                        )
                    ).await()
                }
                println("PostDetailsViewModel - result = $result")
                fetchMessages()
            } catch (exception: Exception) {
//                viewState.update {
//                    it.copy(
//                        createNewMessageState = CreateNewMessageState.Error(
//                            exception.message ?: "Oops, error creating the post"
//                        )
//                    )
//                }
            } finally {
                println("PostDetailsViewModel - createNewMessageState")
            }
        }
    }
}

data class PostDetailsViewState(
    val post: Post?,
    val messagesState: PostMessagesState,
    val messages: Map<String, Message>,
    val newMessage: String
)

sealed class PostMessagesState {
    data object Loading : PostMessagesState()
    data object Success : PostMessagesState()
    data object Empty : PostMessagesState()
    data class Error(val errorMessage: String) : PostMessagesState()
}
