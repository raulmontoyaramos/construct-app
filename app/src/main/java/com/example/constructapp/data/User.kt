package com.example.constructapp.data

data class User(
    val id: String, //Era Long pero lo tuve que cambiar para hacer el constructor
    val firstName: String,
    val lastName: String,
    val appUserPicUrl: String,
    val email: String,
    val avatar: String
) {
    constructor() : this(
        id = "",
        firstName = "",
        lastName = "",
        appUserPicUrl = "",
        email = "",
        avatar = ""
    )
}
