package com.example.shared

interface Syncable<T> {
    fun updateIsSynced(isSynced: Boolean): T
    fun updateServerId(serverId: Int): T
}