package com.example.constructapp.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.constructapp.R
import com.example.constructapp.navigation.Dashboard
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen(
    navController: NavController
) {
    val activity = LocalContext.current as Activity
    val signInLauncher = rememberLauncherForActivityResult(
        contract = FirebaseAuthUIActivityResultContract(),
        onResult = { result: FirebaseAuthUIAuthenticationResult ->
            println("SignInScreen - FirebaseAuthUIActivityResultContract: result = $result")
            println("SignInScreen - FirebaseAuthUIActivityResultContract: resultCode = ${result.resultCode}")
            if (FirebaseAuth.getInstance().currentUser != null) {
                println("SignInScreen - FirebaseAuthUIActivityResultContract: SignIn success!")
                navController.navigate(Dashboard) { launchSingleTop = true }
            } else {
                println("SignInScreen - FirebaseAuthUIActivityResultContract: SignIn cancelled!")
                activity.finish()
            }
        }
    )

    LaunchedEffect(key1 = Unit) {
        AuthUI.getInstance()
            .signOut(activity)
            .addOnCompleteListener {
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

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "ConstrucApp"
            )
        }
    }
}
