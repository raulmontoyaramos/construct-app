package com.example.constructapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.constructapp.data.NetworkService
import com.example.constructapp.data.PostsRepository
import com.example.constructapp.navigation.CreatePost
import com.example.constructapp.navigation.Dashboard
import com.example.constructapp.navigation.PostDetails
import com.example.constructapp.navigation.SignIn
import com.example.constructapp.navigation.parcelableType
import com.example.constructapp.presentation.CreatePostViewModel
import com.example.constructapp.presentation.DashboardViewModel
import com.example.constructapp.presentation.PostDetailsViewModel
import com.example.constructapp.presentation.models.Post
import com.example.constructapp.screens.CreatePostScreen
import com.example.constructapp.screens.DashboardScreen
import com.example.constructapp.screens.PostDetailsScreen
import com.example.constructapp.screens.SignInScreen
import com.example.constructapp.ui.theme.ConstructAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {

    private val postsRepository = PostsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConstructAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (FirebaseAuth.getInstance().currentUser == null) {
                                SignIn
                            } else {
                                Dashboard
                            },
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<SignIn> {
                                SignInScreen(navController = navController)
                            }
                            composable<Dashboard> {
                                val viewModel =
                                    viewModel<DashboardViewModel>(factory = viewModelFactory {
                                        DashboardViewModel(
                                            firebaseAuth = FirebaseAuth.getInstance(),
                                            networkService = NetworkService(
                                                FirebaseFirestore.getInstance()
                                            ),
                                            postsRepository = postsRepository,
                                            navController = navController
                                        )
                                    })
                                DashboardScreen(viewModel)
                            }
                            composable<PostDetails>(
                                typeMap = mapOf(typeOf<Post>() to parcelableType<Post>())
                            ) {
                                val postId = it.toRoute<PostDetails>().postId
                                println("PostDetails - postId=$postId")
                                FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                                    val viewModel: PostDetailsViewModel =
                                        viewModel<PostDetailsViewModel>(factory = viewModelFactory {
                                            PostDetailsViewModel(
                                                networkService = NetworkService(
                                                    FirebaseFirestore.getInstance()
                                                ),
                                                postId = postId,
                                                currentUser = currentUser,
                                                postsRepository = postsRepository,
                                                navController = navController
                                            )
                                        })
                                    PostDetailsScreen(viewModel)
                                } ?: navController.popBackStack()
                            }
                            composable<CreatePost> {
                                FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                                    val viewModel =
                                        viewModel<CreatePostViewModel>(factory = viewModelFactory {
                                            CreatePostViewModel(
                                                currentUser = currentUser,
                                                networkService = NetworkService(
                                                    FirebaseFirestore.getInstance()
                                                ),
                                                navController = navController
                                            )
                                        })
                                    CreatePostScreen(viewModel)
                                } ?: navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <VM : ViewModel> viewModelFactory(createViewModel: () -> VM)
        : ViewModelProvider.Factory {

    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return createViewModel() as T
        }
    }
}
