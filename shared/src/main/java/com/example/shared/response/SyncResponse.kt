package com.example.shared.response

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse<T>(
    val updatedEntities: List<T>
)