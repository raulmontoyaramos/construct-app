package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.CreatePost
import com.example.constructapp.PostDetails
import com.example.constructapp.SignIn
import com.example.constructapp.data.Post
import com.example.constructapp.data.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DashboardViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController,
    private val repository: Repository
) : ViewModel() {

    val viewState = MutableStateFlow(
        DashboardViewState(
            dashboardState = DashboardState.Loading,
            userPicUrl = firebaseAuth.currentUser?.photoUrl?.toString().orEmpty(),
            selectedTab = DashboardTab.POSTS,
            posts = emptyMap()
        )
    )

    init {
        viewModelScope.launch {
            viewState.update { it.copy(dashboardState = DashboardState.Loading) }
            try {
                val postsMap = withContext(Dispatchers.IO) {
                    val postsCollection: CollectionReference = firebaseFirestore.collection("Posts")
                    postsCollection.get().await().associate { document: QueryDocumentSnapshot ->
                        val postData = document.toObject(Post::class.java)
                        println("DashboardViewModel - get - post = $postData")
                        document.id to postData
                    }.also { repository.setPosts(it) }
                }
                println("DashboardViewModel - postsMap = $postsMap")

                if (postsMap.isEmpty()) {
                    viewState.update { it.copy(dashboardState = DashboardState.Empty) }
                } else {
                    viewState.update {
                        it.copy(
                            dashboardState = DashboardState.Success,
                            posts = postsMap
                        )
                    }
                }
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        dashboardState = DashboardState.Error(
                            exception.message ?: "Oops, error loading Posts.."
                        )
                    )
                }
            } finally {
                println("DashboardViewModel - dashboardState = ${viewState.value.dashboardState}")
            }
        }
    }

    fun onCreatePostClicked() =
        navController.navigate(CreatePost)

    fun onSignOutClicked() {
        firebaseAuth.signOut()
        navController.navigate(SignIn) {
            launchSingleTop = true
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun onTabPressed(tab: DashboardTab) {
        viewState.update { it.copy(selectedTab = tab) }
    }

    fun onPostClicked(postId: String) {
        println("DashboardViewModel - onPostClicked - post = $postId")
        navController.navigate(PostDetails(postId))
    }
}

data class DashboardViewState(
    val dashboardState: DashboardState,
    val userPicUrl: String,
    val selectedTab: DashboardTab,
    val posts: Map<String, Post>
)

enum class DashboardTab {
    POSTS, MESSAGES
}

sealed class DashboardState {
    data object Loading : DashboardState()
    data object Success : DashboardState()
    data object Empty : DashboardState()
    data class Error(val errorMessage: String) : DashboardState()
}
