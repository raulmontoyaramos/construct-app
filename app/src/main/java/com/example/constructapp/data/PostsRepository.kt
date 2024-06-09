package com.example.constructapp.data

import com.example.constructapp.presentation.models.Post
import kotlinx.coroutines.flow.MutableStateFlow

class PostsRepository {

    private val posts = MutableStateFlow<Map<String, Post>>(emptyMap())

    suspend fun setPosts(posts: Map<String, Post>) {
        this.posts.value = posts
    }

    suspend fun getPostById(postId: String): Post? = posts.value[postId]
}
