package com.example.constructapp.data

data class Post(
    val userId: String,
    val title: String,
    val description: String
) {
    // Required empty constructor so we can parse the objects from firestore
    constructor() : this("", "", "")
}
