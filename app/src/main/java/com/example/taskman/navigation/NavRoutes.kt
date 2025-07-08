package com.example.taskman.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Main

@Serializable
data class Authentication(val type: String)

@Serializable
object Profile

@Serializable
data class TaskControl(val taskId: Int?)

@Serializable
data class GroupControl(val groupId: Int?)

@Serializable
object SearchScreen

@Serializable
object Settings