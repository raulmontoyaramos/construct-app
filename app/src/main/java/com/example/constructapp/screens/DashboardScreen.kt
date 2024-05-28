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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.constructapp.data.Post
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                    DashboardScreenContent(
                        viewState = viewState,
                        onTabPressed = { viewModel.onTabPressed(it) },
                        onPostClick = {}
                    )

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

@Composable
fun DashboardScreenContent(
    viewState: DashboardViewState,
    onTabPressed: (DashboardTab) -> Unit,
    onPostClick: (Post) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomAppBar {
                TabRow(selectedTabIndex = viewState.selectedTab.ordinal) {
                    DashboardTab.entries.forEach { dashboardTab: DashboardTab ->
                        Tab(text = { Text(dashboardTab.text()) },
                            selected = dashboardTab == viewState.selectedTab,
                            onClick = { onTabPressed(dashboardTab) },
                            icon = {
                                Icon(
                                    imageVector = dashboardTab.icon(),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (viewState.selectedTab) {
                DashboardTab.POSTS ->
                    DashboardPostsScreen(viewState)

                DashboardTab.MESSAGES ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Messages list")
                    }
            }
        }
    }
}

private fun DashboardTab.text() = when (this) {
    DashboardTab.POSTS -> "Posts"
    DashboardTab.MESSAGES -> "Messages"
}

private fun DashboardTab.icon() = when (this) {
    DashboardTab.POSTS -> Icons.Default.Home
    DashboardTab.MESSAGES -> Icons.Default.Email
}

@Composable
private fun DashboardPostsScreen(viewState: DashboardViewState) {
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
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .withZone(ZoneId.systemDefault())
                        Text(text = "User: ${post.userId}")
                        Text(text = "User name: ${post.userName}")
                        Text(text = "Title: ${post.title}")
                        Text(text = "Description: ${post.description}")
                        Text(text = "Created: ${formatter.format(Instant.ofEpochMilli(post.createdAt))}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}