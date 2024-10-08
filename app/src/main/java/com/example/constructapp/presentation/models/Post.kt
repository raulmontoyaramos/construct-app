package com.example.constructapp.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@Parcelize
data class Post(
    val userId: String,
    val userName: String,
    val userPicUrl: String,
    val title: String,
    val description: String,
    val createdAt: Long,
) : Parcelable {
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
