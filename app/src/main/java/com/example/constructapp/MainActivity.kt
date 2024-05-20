package com.example.constructapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.constructapp.ui.theme.ConstructAppTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result: FirebaseAuthUIAuthenticationResult? ->
            println("MainActivity - FirebaseAuthUIActivityResultContract: result = $result")
            if (FirebaseAuth.getInstance().currentUser != null) {
                println("MainActivity - FirebaseAuthUIActivityResultContract: SignIn success!")
                navController.navigate(Dashboard) { launchSingleTop = true }
            } else {
                println("MainActivity - FirebaseAuthUIActivityResultContract: SignIn failed!")
            }
        }

    private val signInIntent: Intent = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(
            listOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build(),
            )
        )
        .setLogo(R.drawable.ic_launcher_foreground) // Set logo drawable
        .setTheme(R.style.AppTheme) // Set theme
        .build()

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConstructAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    val currentBackStack by navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStack?.destination
                    val currentScreen = currentDestination?.route
                    println("MainActivity - currentScreen = $currentScreen")

                    Scaffold { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = SignIn,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<SignIn> {
                                if (FirebaseAuth.getInstance().currentUser != null) {
                                    println("MainActivity - SignIn: user already signed in!")
                                    navController.navigate(Dashboard) { launchSingleTop = true }
                                } else {
                                    println("MainActivity - SignIn: user should sign in!")
                                    SignInScreen(signInWithGoogleClicked = ::signIn)
                                }
                            }
                            composable<Dashboard> {
                                DashboardScreen(
                                    createPostClicked = {
                                        navController.navigate(CreatePost) {
                                            launchSingleTop = true
                                        }
                                    },
                                    signOutClicked = {
                                        signOut {
                                            navController.navigate(SignIn) {
                                                launchSingleTop = true
                                                popUpTo(navController.graph.id) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    })
                            }
                            composable<CreatePost> {
                                CreatePostScreen(createPostClicked = ::createPost)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun signIn() = signInLauncher.launch(signInIntent)

    private fun signOut(navigateToSignIn: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        navigateToSignIn()
    }

    private fun createPost() {
        // TODO: setup firebase first
        println("Sending post to firebase...")
    }
}

@Composable
fun SignInScreen(signInWithGoogleClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = signInWithGoogleClicked) {
            Text(text = "Sign in with Google")
        }
    }
}

@Composable
fun DashboardScreen(
    createPostClicked: () -> Unit,
    signOutClicked: () -> Unit
) = Scaffold(
    floatingActionButton = {
        FloatingActionButton(onClick = createPostClicked) {
            Icon(Icons.Rounded.Create, "Create new post")
        }
    }
) { innerPadding ->
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Welcome to ConstructApp")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = signOutClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Sign out!")
            }
        }
    }
}

@Composable
fun CreatePostScreen(createPostClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(text = "Create new post:")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = createPostClicked,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(text = "Create post!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ConstructAppTheme {
        DashboardScreen({}, {})
    }
}
