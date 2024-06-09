package com.example.constructapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.data.NetworkService
import com.example.constructapp.presentation.models.Post
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class CreatePostViewModel(
    private val currentUser: FirebaseUser,
    private val networkService: NetworkService,
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
                withContext(Dispatchers.IO) {
                    networkService.addPost(
                        Post(
                            userId = currentUser.uid,
                            userName = currentUser.displayName ?: "Unknown",
                            userPicUrl = currentUser.photoUrl.toString(),
                            title = viewState.value.title,
                            description = viewState.value.description,
                            createdAt = Instant.now().epochSecond
                        )
                    )
                }
                println("CreatePostViewModel - result = success")
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

    fun onOkClicked() = navController.popBackStack()

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
