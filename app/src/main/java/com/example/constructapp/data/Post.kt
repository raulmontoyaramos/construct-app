package com.example.constructapp.data

import java.time.Instant

data class Post(
    val userId: String,
    val userName: String,
    val userPicUrl: String,
    val title: String,
    val description: String,
    val createdAt: Long,
) {
    // Required empty constructor so we can parse the objects from firestore
    constructor() : this(
        userId = "",
        userName = "",
        userPicUrl = "",
        title = "",
        description = "",
        createdAt = Instant.EPOCH.toEpochMilli()
    )
}
