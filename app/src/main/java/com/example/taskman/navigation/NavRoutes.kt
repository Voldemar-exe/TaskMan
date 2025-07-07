package com.example.taskman.navigation

import kotlinx.serialization.Serializable


@Serializable
object Profile

@Serializable
object Main

@Serializable
data class Authentication(val type: String)

@Serializable
object SearchScreen

@Serializable
object Splash