package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.constructapp.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

class PostDetailsViewModel(
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController,
    private val post: Post
) : ViewModel() {

    val viewState = MutableStateFlow(
        DetailsViewState(
            post = post
        )
    )

    fun onBackButtonClicked() = navController.popBackStack()

    fun onReplyButtonClicked() = Unit
}

data class DetailsViewState(
    val post: Post
)
