package com.example.auth


import kotlinx.serialization.Serializable

@Serializable
data class DeleteResponse(
    val isDelete: Boolean
)