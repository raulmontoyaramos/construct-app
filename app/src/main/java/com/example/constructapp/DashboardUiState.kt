package com.example.constructapp

import com.example.constructapp.models.Post
import com.example.constructapp.utils.DashboardTab

data class DashboardUiState(
    val selectedTab: DashboardTab,
    val postList: List<Post>,
    val postMessages: Map<Post, List<String>>,
)
