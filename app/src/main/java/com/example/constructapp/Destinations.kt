package com.example.constructapp

import com.example.constructapp.data.Post
import kotlinx.serialization.Serializable

@Serializable
object SignIn

@Serializable
object Dashboard

@Serializable
object CreatePost

@Serializable
data class PostDetails(
    val post: Post
)

@Serializable
object MessagesScreen
