package com.example.routing

import com.example.db.repository.SyncRepositoryImpl
import com.example.db.repository.UserRepositoryImpl
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

fun Application.configureDataRouting() {
    val userRepository = UserRepositoryImpl()
    val syncRepository = SyncRepositoryImpl()

    routing {
        authenticate("auth-jwt") {
            userRoute(userRepository)
            syncRoute(syncRepository)
        }
    }
}
