package com.example.auth

import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureAuthRouting() {

    val authService: AuthService by inject<AuthService>()

    routing {
        post("/login") {
            val credentials = call.receive<LoginReceiveRemote>()
            val result = authService.login(credentials)
            result.fold(
                onSuccess = { call.respondText(it.toString()) },
                onFailure = { call.respondText(it.message ?: "Login failed") }
            )
        }
        post("/register") {
            val credentials = call.receive<RegisterReceiveRemote>()
            val result = authService.register(credentials)
            result.fold(
                onSuccess = { call.respondText(it.toString()) },
                onFailure = { call.respondText(it.message ?: "Registration failed") }
            )
        }
    }
}
