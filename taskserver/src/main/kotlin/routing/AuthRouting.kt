package com.example.routing

import com.example.auth.AuthService
import com.example.auth.LoginReceiveRemote
import com.example.auth.RegisterReceiveRemote
import com.example.db.TokenRepositoryImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt

fun Application.configureAuthRouting() {

    val authService: AuthService by inject<AuthService>()
    val tokenRepository = TokenRepositoryImpl()
    routing {
        post("/login") {
            val request = call.receive<LoginReceiveRemote>()
            val result = authService.findByLogin(request.login)
            result.fold(
                onSuccess = { userDto ->
                    if (!BCrypt.checkpw(request.password, userDto.passwordHash)) {
                        call.respond(HttpStatusCode.Unauthorized, "Неверный пароль")
                        return@post
                    }
                    tokenRepository.getToken(request.login)?.let {
                        call.respond(mapOf("token" to it))
                    }
                },
                onFailure = { call.respond(HttpStatusCode.Unauthorized, "User not found") }
            )
        }
        post("/register") {
            val credentials = call.receive<RegisterReceiveRemote>()
            val result = authService.register(credentials)
            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = {
                    call.respond(
                        ErrorResponse("UserExistsError", "User with this login already exists")
                    )
                }
            )
        }
        /*post("/login") {
            val credentials = call.receive<LoginReceiveRemote>()
            val result = authService.login(credentials)
            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = {
                    call.respond(
                        ErrorResponse("InvalidCredentialsError", "Invalid login or password")
                    )
                }
            )
        }
        post("/register") {
            val credentials = call.receive<RegisterReceiveRemote>()
            val result = authService.register(credentials)
            result.fold(
                onSuccess = { call.respond(it) },
                onFailure = {
                    call.respond(
                        ErrorResponse("UserExistsError", "User with this login already exists")
                    )
                }
            )
        }
        delete("/users/{login}") {
            val login =
                call.parameters["login"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            try {
                val result = authService.deleteByLogin(login)
                result.fold(
                    onSuccess = {
                        call.respond(it)
                    },
                    onFailure = {
                        call.respond(
                            ErrorResponse("UserNotExistsError", "User with this login not exists")
                        )
                    }
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete user")
            }
        }*/
    }
}

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String
)