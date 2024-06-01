package com.example.constructapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
object SignIn

@Serializable
object Dashboard

@Serializable
object CreatePost

@Serializable
@Parcelize
data class PostDetails(
    val postId: String
) : Parcelable

@Serializable
object MessagesScreen
