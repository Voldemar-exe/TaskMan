package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse<T>(
    val code: Int = 200,
    val message: String? = "Processed Successfully",
    val data: T? = null
)