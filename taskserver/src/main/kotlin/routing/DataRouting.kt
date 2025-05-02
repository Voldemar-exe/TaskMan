package com.example.routing

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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail


fun Application.configureDataRouting() {

    val taskRepository = TaskRepositoryImpl()

    val testLogin = "test_user"

    routing {
        route("/tasks") {
            get {
                val tasks = taskRepository.allTasks(testLogin)
                call.respond(
                    ServerResponse(data = tasks)
                )
            }
            post {
                val task = call.receive<TaskDto>()
                taskRepository.addTask(testLogin, task)
                call.respond(HttpStatusCode.Created)
            }
            post("/{id}") {
                val task = call.receive<TaskDto>()
                taskRepository.updateTask(testLogin, task)
                call.respond(HttpStatusCode.UpgradeRequired)
            }
            delete("/{id}") {
                val id = call.parameters.getOrFail("id")
                val success = taskRepository.removeTask(testLogin, id.toInt())
                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}