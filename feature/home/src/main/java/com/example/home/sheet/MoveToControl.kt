package com.example.home.sheet

sealed interface MoveToControl {
    data object None : MoveToControl
    data class Task(val taskId: Int? = null) : MoveToControl
    data class Group(val groupId: Int? = null) : MoveToControl
}