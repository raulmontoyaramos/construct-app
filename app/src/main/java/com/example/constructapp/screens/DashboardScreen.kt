package com.example.constructapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val viewState: DashboardViewState = viewModel.viewState.collectAsState().value
    println("DashboardScreen - viewState = $viewState")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Posts") },
                actions = {
                    IconButton(onClick = viewModel::onSignOutClicked) {
                        Icon(Icons.Filled.ExitToApp, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCreatePostClicked) {
                Icon(Icons.Rounded.Create, "Create new post")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (viewState.dashboardState) {
                DashboardState.Loading ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(64.dp))
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Loading posts..")
                    }

                DashboardState.Success ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(viewState.posts) { post ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(text = "User: ${post.userId}")
                                        Text(text = "Title: ${post.title}")
                                        Text(text = "Description: ${post.description}")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                DashboardState.Empty ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No posts available yet..")
                    }

                is DashboardState.Error ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = viewState.dashboardState.errorMessage)
                    }
            }
        }
    }
}