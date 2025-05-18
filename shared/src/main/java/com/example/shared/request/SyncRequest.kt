package com.example.shared.request

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest<T>(
    val entitiesToUpdate: List<T>,
    val allEntitiesIds: List<Int>
)
