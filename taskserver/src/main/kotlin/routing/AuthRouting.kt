package com.example.routing

import com.example.auth.AuthService
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureAuthRouting() {

    val authService: AuthService by inject<AuthService>()

    routing {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val result = authService.login(request)
            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = { call.respond(HttpStatusCode.Unauthorized, "User not found") }
            )
        }
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val result = authService.register(request)
            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = { call.respond(HttpStatusCode.Unauthorized, "User exists") }
            )
        }
    }
}