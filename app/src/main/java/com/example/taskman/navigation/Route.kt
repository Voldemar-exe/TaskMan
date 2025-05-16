package com.example.taskman.navigation

import kotlinx.serialization.Serializable


@Serializable
data class Profile(val name: String)

@Serializable
object Main

@Serializable
data class Authentication(val type: String)

@Serializable
object SearchScreen

@Serializable
object Splash