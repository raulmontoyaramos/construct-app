package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.data.Post
import com.example.constructapp.data.Repository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostDetailsViewModel(
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController,
    private val postId: String,
    private val repository: Repository
) : ViewModel() {

    val viewState = MutableStateFlow(
        PostDetailsViewState(
            post = null
        )
    )

    init {
        viewModelScope.launch {
            val post: Post? = withContext(Dispatchers.IO) { repository.getPostById(postId) }
            viewState.update { it.copy(post = post) }
        }
    }

    fun onBackButtonClicked() = navController.navigateUp()

    fun onReplyButtonClicked() = Unit
}

data class PostDetailsViewState(
    val post: Post?
)
