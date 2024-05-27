package com.example.constructapp.utils

import androidx.lifecycle.ViewModel
import com.example.constructapp.DashboardUiState
import com.example.constructapp.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            DashboardUiState(
                selectedTab = DashboardTab.POSTS,
                postList = emptyList(),
                postMessages = emptyMap()
            )
        )
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        initializeUIState()
    }

    private fun initializeUIState() {
        //Cargaremos Request de los Post y conversaciones mediante Fetchs a Firebase
    }

    fun onPostClicked(post: Post) {
        //Navigate to post details when a Post is clicked
    }

    fun onTabSelected(dashboardTab: DashboardTab) {
        _uiState.update {
            it.copy(
                selectedTab = dashboardTab
            )
        }
    }
}

enum class DashboardTab {
    POSTS, POSTS_MESSAGES
}
