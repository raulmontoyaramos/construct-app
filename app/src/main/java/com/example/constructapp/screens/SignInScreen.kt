package com.example.constructapp.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.constructapp.Dashboard
import com.example.constructapp.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(
    navController: NavController
) {
    val activity = LocalContext.current as Activity
    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract(),
        onResult = { result ->
            println("SignInScreen - FirebaseAuthUIActivityResultContract: result = $result")
            println("SignInScreen - FirebaseAuthUIActivityResultContract: resultCode = ${result.resultCode}")
            if (FirebaseAuth.getInstance().currentUser != null) {
                println("SignInScreen - FirebaseAuthUIActivityResultContract: SignIn success!")
                navController.navigate(Dashboard) { launchSingleTop = true }
            } else {
                println("SignInScreen - FirebaseAuthUIActivityResultContract: SignIn failed!")
                activity.finish()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        signInLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.GoogleBuilder().build(),
                        // AuthUI.IdpConfig.EmailBuilder().build(),
                    )
                )
                .setLogo(R.drawable.logo)
                .setTheme(R.style.Theme_ConstructApp)
                .build()
        )
    }
}
