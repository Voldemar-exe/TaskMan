package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
sealed interface ItemIcon {
    val ru: String
}