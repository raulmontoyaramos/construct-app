package com.example.constructapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@Parcelize
data class Message(
    val userId: String,
    val userName: String,
    val userPicUrl: String,
    val message: String,
    val createdAt: Long,
) : Parcelable {
    // Required empty constructor so we can parse the objects from firestore
    constructor() : this(
        userId = "",
        userName = "",
        userPicUrl = "",
        message = "",
        createdAt = Instant.EPOCH.toEpochMilli()
    )
}
