package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.constructapp.CreatePost
import com.example.constructapp.PostDetails
import com.example.constructapp.SignIn
import com.example.constructapp.data.Post
import com.example.constructapp.data.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    private val navController: NavHostController,
    private val repository: Repository
) : ViewModel() {

    val viewState = MutableStateFlow(
        DashboardViewState(
            userPicUrl = firebaseAuth.currentUser?.photoUrl?.toString().orEmpty(),
            selectedTab = DashboardTab.POSTS,
            posts = emptyMap(),
            dashboardPostsState = DashboardPostsState.Loading,
            isRefreshingPosts = false,
            postsFiltered = emptyMap(),
            dashboardPostsStateFiltered = DashboardPostsState.Loading,
            isRefreshingPostsFiltered = false
        )
    )

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            viewState.update {
                it.copy(
                    dashboardPostsState = DashboardPostsState.Loading,
                    isRefreshingPosts = true
                )
            }
            try {
                val postsMap = withContext(Dispatchers.IO) {
                    firebaseFirestore.collection("Posts")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get().await()
                        .associate { document: QueryDocumentSnapshot ->
                            document.id to document.toObject(Post::class.java)
                        }.also { repository.setPosts(it) }
                }
                println("DashboardViewModel - postsMap = $postsMap")

                viewState.update {
                    it.copy(
                        dashboardPostsState = DashboardPostsState.Success,
                        posts = postsMap,
                        isRefreshingPosts = false
                    )
                }
            } catch (exception: Exception) {
                viewState.update {
                    it.copy(
                        dashboardPostsState = DashboardPostsState.Error(
                            exception.message ?: "Oops, error loading Posts.."
                        ),
                        isRefreshingPosts = false
                    )
                }
            } finally {
                println("DashboardViewModel - dashboardState = ${viewState.value.dashboardPostsState}")
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
    val userPicUrl: String,
    val selectedTab: DashboardTab,
    val posts: Map<String, Post>,
    val dashboardPostsState: DashboardPostsState,
    val isRefreshingPosts: Boolean,
    val postsFiltered: Map<String, Post>,
    val dashboardPostsStateFiltered: DashboardPostsState,
    val isRefreshingPostsFiltered: Boolean
)

enum class DashboardTab {
    POSTS, MESSAGES
}

sealed class DashboardPostsState {
    data object Loading : DashboardPostsState()
    data object Success : DashboardPostsState()
    data class Error(val errorMessage: String) : DashboardPostsState()
}
