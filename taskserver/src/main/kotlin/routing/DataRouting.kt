package com.example.routing

import com.example.auth.User
import com.example.auth.UserRepositoryImpl
import com.example.db.TaskRepositoryImpl
import com.example.dto.request.TaskDto
import com.example.dto.response.ServerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail


fun Application.configureDataRouting() {

    val taskRepository = TaskRepositoryImpl()

    val testUser = User(
        login = "test_login",
        passwordHash = "hash",
        username = "Test User",
        email = "test@mail"
    )

    // TODO REMOVE AFTER TESTS
    UserRepositoryImpl().findByLogin(testUser.login) ?: UserRepositoryImpl().createUser(
        testUser.login,
        testUser.passwordHash,
        testUser.username,
        testUser.email
    )

    routing {
        route("/tasks") {
            get {
                val tasks = taskRepository.allTasks(testUser.login)
                call.respond(
                    ServerResponse(data = tasks)
                )
            }
            post {
                val task = call.receive<TaskDto>()
                taskRepository.addTask(testUser.login, task)
                call.respond(HttpStatusCode.Created)
            }
            route("/{id}") {
                put {
                    val task = call.receive<TaskDto>()
                    taskRepository.updateTask(testUser.login, task)
                    call.respond(HttpStatusCode.UpgradeRequired)
                }
                delete {
                    val id = call.parameters.getOrFail("id").toInt()
                    val success = taskRepository.removeTask(testUser.login, id)
                    if (success) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}