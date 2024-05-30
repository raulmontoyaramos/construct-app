package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.constructapp.CreatePost
import com.example.constructapp.PostDetails
import com.example.constructapp.SignIn
import com.example.constructapp.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DashboardViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val navController: NavController
) : ViewModel() {

    val viewState = MutableStateFlow(
        DashboardViewState(
            dashboardState = DashboardState.Loading,
            selectedTab = DashboardTab.POSTS,
            posts = emptyList()
        )
    )

    init {
        viewModelScope.launch {
            viewState.update { it.copy(dashboardState = DashboardState.Loading) }
            try {
                val posts: MutableList<Post> = withContext(Dispatchers.IO) {
                    val postsDatabase: CollectionReference = firebaseFirestore.collection("Posts")
                    postsDatabase.get().await().toObjects(Post::class.java)
                }
                println("DashboardViewModel - posts = $posts")

                if (posts.isEmpty()) {
                    viewState.update { it.copy(dashboardState = DashboardState.Empty) }
                } else {
                    viewState.update {
                        it.copy(
                            dashboardState = DashboardState.Success,
                            posts = posts
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
        navController.navigate(CreatePost) {
            launchSingleTop = true
        }

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

    fun onPostClicked(post: Post) {
        navController.navigate(PostDetails(post)) {
            launchSingleTop = true
        }
    }
}

data class DashboardViewState(
    val dashboardState: DashboardState,
    val selectedTab: DashboardTab,
    val posts: List<Post>
)

enum class DashboardTab {
    POSTS, MESSAGES
}
//data class DashboardTabInfo(
//    val tab: DashboardTab,
//    val icon: ImageVector,
//    val text: String
//) {
//    abstract val icon: ImageVector
//    abstract val text: String
//    data class Posts(
//        override val icon: ImageVector = Icons.Default.Home,
//        override val text: String = "Posts"
//    ): DashboardTab()
//    data class Messages(
//        override val icon: ImageVector = Icons.Default.Email,
//        override val text: String = "Messages"
//    ): DashboardTab()
//}

sealed class DashboardState {
    data object Loading : DashboardState()
    data object Success : DashboardState()
    data object Empty : DashboardState()
    data class Error(val errorMessage: String) : DashboardState()
}
