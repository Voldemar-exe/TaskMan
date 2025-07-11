package com.example.home.sheet

sealed interface HomeDestination {
    data object Home : HomeDestination
    data class TaskControl(val taskId: Int? = null) : HomeDestination
    data class GroupControl(val groupId: Int? = null) : HomeDestination
}