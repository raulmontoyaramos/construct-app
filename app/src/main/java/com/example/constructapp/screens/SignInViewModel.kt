package com.example.constructapp.screens

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.constructapp.Dashboard
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

class SignInViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val navController: NavController
) : ViewModel() {

    val viewState = MutableStateFlow(
        SignInViewState(userSignedIn = firebaseAuth.currentUser != null)
    )

    init {
        if (viewState.value.userSignedIn) {
            println("SignInViewModel - SignIn: user already signed in!")
            navController.navigate(Dashboard) { launchSingleTop = true }
        } else {
            println("SignInViewModel - SignIn: user needs to sign in!")
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        println("SignInViewModel - FirebaseAuthUIActivityResultContract: result = $result")
        println("SignInViewModel - FirebaseAuthUIActivityResultContract: resultCode = ${result.resultCode}")
        if (firebaseAuth.currentUser != null) {
            println("SignInViewModel - FirebaseAuthUIActivityResultContract: SignIn success!")
            navController.navigate(Dashboard) { launchSingleTop = true }
        } else {
            println("SignInViewModel - FirebaseAuthUIActivityResultContract: SignIn failed!")
        }
    }
}

data class SignInViewState(
    val userSignedIn: Boolean
)
