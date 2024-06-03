package com.example.constructapp.data

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
    val commentTimeStamp: Long,
) : Parcelable {
    // Required empty constructor so we can parse the objects from firestore
    constructor() : this(
        userId = "",
        userName = "",
        userPicUrl = "",
        body = "",
        commentTimeStamp = Instant.EPOCH.toEpochMilli()
    )
}
