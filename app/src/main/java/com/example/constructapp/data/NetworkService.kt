package com.example.constructapp.data

import com.example.constructapp.presentation.models.Comment
import com.example.constructapp.presentation.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await

interface NetworkService {

    suspend fun getPost(postId: String): Post?

    suspend fun getPosts(): Map<String, Post>

    suspend fun getComments(postId: String): Map<String, Comment>

    suspend fun getMyLastCommentsInPosts(userId: String): Map<String, Comment>

    suspend fun addPost(post: Post)

    suspend fun addComment(postId: String, comment: Comment)

    suspend fun removePost(postId: String)

    suspend fun removeComment(postId: String, commentId: String)

    companion object {

        operator fun invoke(firestore: FirebaseFirestore) =
            object : NetworkService {
                override suspend fun getPost(postId: String) =
                    firestore
                        .collection("Posts")
                        .document(postId)
                        .get()
                        .await()
                        .toObject(Post::class.java)

                override suspend fun getPosts() =
                    firestore
                        .collection("Posts")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .associate { document: QueryDocumentSnapshot ->
                            document.id to document.toObject(Post::class.java)
                        }

                override suspend fun getComments(postId: String) =
                    firestore
                        .collection("Posts")
                        .document(postId)
                        .collection("Messages")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .associate { document: QueryDocumentSnapshot ->
                            document.id to document.toObject(Comment::class.java)
                        }

                override suspend fun getMyLastCommentsInPosts(userId: String) =
                    firestore
                        .collectionGroup("Messages")
                        .whereEqualTo("userId", userId)
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .get()
                        .await()
                        .toObjects(Comment::class.java)
                        .associateBy { it.postId }

                override suspend fun addPost(post: Post) {
                    firestore
                        .collection("Posts")
                        .add(post)
                        .await()
                }


                override suspend fun addComment(postId: String, comment: Comment) {
                    firestore
                        .collection("Posts")
                        .document(postId)
                        .collection("Messages")
                        .add(comment)
                        .await()
                }

                override suspend fun removePost(postId: String) {
                    firestore
                        .collection("Posts")
                        .document(postId)
                        .delete()
                        .await()
                }

                override suspend fun removeComment(postId: String, commentId: String) {
                    firestore
                        .collection("Posts")
                        .document(postId)
                        .collection("Messages")
                        .document(commentId)
                        .delete()
                        .await()
                }
            }
    }
}