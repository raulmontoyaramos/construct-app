package com.example.constructapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.constructapp.DashboardUiState
import com.example.constructapp.HomeTopBar
import com.example.constructapp.models.Post
import com.example.constructapp.utils.DashboardTab
import com.example.constructapp.utils.DashboardViewModel

//Scaffold con topBar(Meter en topbar la función para ello q está en otro archivo)
//Lo quería como lo he hecho en la función de abajo??
@Composable
fun DashboardScreen() {

    val viewModel: DashboardViewModel = viewModel()
    val dashboardUiState = viewModel.uiState.collectAsState().value
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Scaffold (topBar = { HomeTopBar() }){ paddingValues ->
            DashboardScreenContent(
                modifier = Modifier.padding(paddingValues),
                dashboardUiState = dashboardUiState,
                onTabPressed = { dashboardTab: DashboardTab ->
                    viewModel.onTabSelected(dashboardTab = dashboardTab)
                },
                onPostClick = { post: Post ->
                    viewModel.onPostClicked(
                        post = post
                    )
                }
            )
        }
    }
}

@Composable
fun DashboardScreenContent(
    modifier: Modifier,
    dashboardUiState: DashboardUiState,
    onTabPressed: (DashboardTab) -> Unit,
    onPostClick: (Post) -> Unit
) {
    val dashboardTabInfoList = listOf(
        DashboardTabInfo(
            tab = DashboardTab.POSTS,
            icon = Icons.Default.Inbox,
            text = "Posts"
        ),
        DashboardTabInfo(
            tab = DashboardTab.POSTS_MESSAGES,
            icon = Icons.Default.Message,
            text = "Messages"
        )
    )
}

data class DashboardTabInfo(
    val tab: DashboardTab,
    val icon: ImageVector,
    val text: String
)
