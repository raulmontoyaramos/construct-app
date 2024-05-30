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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.constructapp.data.Post
import com.example.constructapp.navigation.parcelableType
import com.example.constructapp.screens.CreatePostScreen
import com.example.constructapp.screens.CreatePostViewModel
import com.example.constructapp.screens.DashboardScreen
import com.example.constructapp.screens.DashboardViewModel
import com.example.constructapp.screens.PostDetailsScreen
import com.example.constructapp.screens.PostDetailsViewModel
import com.example.constructapp.screens.SignInScreen
import com.example.constructapp.screens.SignInViewModel
import com.example.constructapp.ui.theme.ConstructAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConstructAppTheme {
                // A surface container using the 'background' color from the theme
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
                                SignInScreen(
                                    SignInViewModel(
                                        firebaseAuth = FirebaseAuth.getInstance(),
                                        navController = navController
                                    )
                                )
                            }
                            composable<Dashboard> {
                                DashboardScreen(
                                    DashboardViewModel(
                                        firebaseAuth = FirebaseAuth.getInstance(),
                                        firebaseFirestore = FirebaseFirestore.getInstance(),
                                        navController = navController
                                    )
                                )
                            }
                            composable<PostDetails>(
                                typeMap = mapOf(typeOf<Post>() to parcelableType<Post>())
                            ) {
                                val post = it.toRoute<PostDetails>().post
                                println("PostDetails - post=$post")
                                PostDetailsScreen(
                                    PostDetailsViewModel(
                                        firebaseFirestore = FirebaseFirestore.getInstance(),
                                        navController = navController,
                                        post = post
                                    )
                                )
                            }
                            composable<CreatePost> {
                                FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                                    CreatePostScreen(
                                        CreatePostViewModel(
                                            firebaseAuth = FirebaseAuth.getInstance(),
                                            currentUser = currentUser,
                                            firebaseFirestore = FirebaseFirestore.getInstance(),
                                            navController = navController
                                        )
                                    )
                                } ?: navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}
