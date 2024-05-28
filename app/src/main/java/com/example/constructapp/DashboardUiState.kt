package com.example.constructapp

import com.example.constructapp.data.Post
import com.example.constructapp.screens.DashboardTab

data class DashboardUiState(
    val selectedTab: DashboardTab,
    val postList: List<Post>,
    val postMessages: Map<Post, List<String>>,
)
