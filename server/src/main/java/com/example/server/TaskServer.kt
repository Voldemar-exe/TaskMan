package com.example.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing


fun main() {
    embeddedServer(Netty, port = 8080) {
        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() {
    routing {
        // Получение всех пользователей
        get("/users") {
            call.respond(getAllUsers())
        }

        // Создание нового пользователя
        post("/users") {
            val user = call.receive<User>()
            val userId = createUser(user.username, user.email.toString(), user.passwordHash)
            call.respond(mapOf("id" to userId, "message" to "User created"))
        }

        // Получение пользователя по ID
        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }
            val user = getUserById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(user)
            }
        }

        put("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }
            val user = call.receive<User>()
            val updatedRows = updateUser(id, user.username, user.email ?: "", user.passwordHash)
            if (updatedRows == 0) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(mapOf("message" to "User updated"))
            }
        }

        delete("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            val deletedRows = deleteUser(id)
            if (deletedRows == 0) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(mapOf("message" to "User deleted"))
            }
        }
    }
}