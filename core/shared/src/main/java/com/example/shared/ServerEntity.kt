package com.example.shared

abstract class ServerEntity {
    abstract val localId: Int
    abstract val serverId: Int?
    abstract val name: String
}