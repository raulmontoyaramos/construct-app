package com.example.constructapp.data

import kotlinx.coroutines.flow.MutableStateFlow

class Repository {

    private val posts = MutableStateFlow<Map<String, Post>>(emptyMap())

    suspend fun setPosts(posts: Map<String, Post>) {
        this.posts.value = posts
    }

    suspend fun getPosts(): Map<String, Post> = posts.value

    suspend fun getPostById(postId: String): Post? = posts.value[postId]
}
