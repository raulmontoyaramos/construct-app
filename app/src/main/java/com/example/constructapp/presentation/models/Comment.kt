package com.example.constructapp.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@Parcelize
data class Comment(
    val userId: String,
    val userName: String,
    val userPicUrl: String,
    val body: String,
    val createdAt: Long,
    val postId: String,
    val postTitle: String
) : Parcelable {
    // Required empty constructor so we can parse the objects from firestore
    constructor() : this(
        userId = "",
        userName = "",
        userPicUrl = "",
        body = "",
        createdAt = Instant.EPOCH.toEpochMilli(),
        postId = "",
        postTitle = ""
    )
}
