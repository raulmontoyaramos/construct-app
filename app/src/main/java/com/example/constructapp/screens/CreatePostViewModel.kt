package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant

class CreatePostViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val currentUser: FirebaseUser,
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController
) : ViewModel() {

    val viewState = MutableStateFlow(
        CreatePostViewState(
            createPostState = CreatePostState.Filling,
            userName = currentUser.displayName ?: currentUser.uid,
            userImageUrl = currentUser.photoUrl.toString(),
            title = "",
            description = ""
        )
    )

    fun onCreatePostClicked() {
        viewModelScope.launch {
            viewState.update { it.copy(createPostState = CreatePostState.Creating) }
            try {
                val result: DocumentReference = withContext(Dispatchers.IO) {
                    val postsDatabase = firebaseFirestore.collection("Posts")
                    println("onCreatePostClicked - photoUrl = ${currentUser.photoUrl.toString()}")
                    postsDatabase.add(
                        Post(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            userPicUrl = currentUser.photoUrl.toString(),
                            title = viewState.value.title,
                            description = viewState.value.description,
                            createdAt = Instant.now().epochSecond
                        )
                    ).await()
                }
                println("CreatePostViewModel - result = $result")
                viewState.update { it.copy(createPostState = CreatePostState.Success) }
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        createPostState = CreatePostState.Error(
                            exception.message ?: "Oops, error creating the post"
                        )
                    )
                }
            } finally {
                println("CreatePostViewModel - createPostState = ${viewState.value.createPostState}")
            }
        }
    }

    fun onBackButtonClicked() = navController.navigateUp()

    fun onOkClicked() = navController.navigateUp()

    fun onTitleUpdated(newTitle: String) {
        viewState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionUpdated(newDescription: String) {
        viewState.update { it.copy(description = newDescription) }
    }

    fun onDismissRequest() {
        viewState.update { it.copy(createPostState = CreatePostState.Filling) }
    }
}

data class CreatePostViewState(
    val createPostState: CreatePostState,
    val userName: String,
    val userImageUrl: String,
    val title: String,
    val description: String
)

sealed class CreatePostState {
    data object Filling : CreatePostState()
    data object Creating : CreatePostState()
    data object Success : CreatePostState()
    data class Error(val errorMessage: String) : CreatePostState()
}
