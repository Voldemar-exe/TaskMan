package com.example.routing

import com.example.db.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.route

fun Route.userRoute(userRepository: UserRepository) {
    route("/users") {
        delete {
            val principal = call.principal<JWTPrincipal>()!!
            val login = principal.payload.getClaim("userId").asString()
            userRepository.deleteDataByLogin(login).fold(
                onSuccess = {
                    userRepository.deleteByLogin(login).fold(
                        onSuccess = {
                            call.respond(HttpStatusCode.OK, it)
                        },
                        onFailure = {
                            call.respond(HttpStatusCode.NotFound, it.message ?: "Failed")
                        }
                    )
                },
                onFailure = { call.respond(HttpStatusCode.NotFound, it.message ?: "Failed") }
            )
        }
        route("/data") {
            delete {
                val principal = call.principal<JWTPrincipal>()!!
                val login = principal.payload.getClaim("userId").asString()
                userRepository.deleteDataByLogin(login).fold(
                    onSuccess = {
                        call.respond(HttpStatusCode.OK, it)
                    },
                    onFailure = { call.respond(HttpStatusCode.NotFound, it.message ?: "Failed") }
                )
            }
        }
    }
}
