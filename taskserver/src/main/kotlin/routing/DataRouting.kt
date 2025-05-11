package com.example.routing

import com.example.db.dao.UserDAO
import com.example.db.repository.GroupRepositoryImpl
import com.example.db.repository.TaskRepositoryImpl
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.shared.response.ServerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail

// TODO REDUCE CODE, CREATE SERVICE FOR TASK AND GROUP
fun Application.configureDataRouting() {

    val taskRepository = TaskRepositoryImpl()
    val groupRepository = GroupRepositoryImpl()

    routing {
        authenticate("auth-jwt") {
            route("/tasks") {
                get {
                    val principal = call.principal<JWTPrincipal>()!!
                    val login = principal.payload.getClaim("userId").asString()
                    val tasks = taskRepository.getAllTasksForUser(login)
                    call.respond(
                        ServerResponse(data = tasks)
                    )
                }
                post {
                    val principal = call.principal<JWTPrincipal>()!!
                    val login = principal.payload.getClaim("userId").asString()
                    val task = call.receive<TaskDto>()
                    UserDAO.findById(login)?.let {
                        taskRepository.createTask(it, task).also {
                            call.respond(it)
                        }
                    }
                }
                route("/{id}") {
                    put {
                        val principal = call.principal<JWTPrincipal>()!!
                        val login = principal.payload.getClaim("userId").asString()
                        val task = call.receive<TaskDto>()
                        taskRepository.updateTask(login, task)
                        call.respond(HttpStatusCode.OK)
                    }
                    delete {
                        val principal = call.principal<JWTPrincipal>()!!
                        val login = principal.payload.getClaim("userId").asString()
                        val id = call.parameters.getOrFail("id").toInt()
                        val success = taskRepository.deleteTask(login, id)
                        if (success) {
                            call.respond(HttpStatusCode.OK)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                }
            }

            route("/groups") {
                get {
                    val principal = call.principal<JWTPrincipal>()!!
                    val login = principal.payload.getClaim("userId").asString()
                    val groups = groupRepository.getGroupsForUser(login)
                    call.respond(
                        ServerResponse(data = groups)
                    )
                }
                post {
                    val principal = call.principal<JWTPrincipal>()!!
                    val login = principal.payload.getClaim("userId").asString()
                    try {
                        val group = call.receive<GroupDto>()
                        UserDAO.findById(login)?.let {
                            groupRepository.createGroup(it, group).let {
                                groupRepository.syncTasksForGroup(
                                    login,
                                    it,
                                    group.tasks.map { it.id }
                                )
                                call.respond(it)
                            }
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                    }
                }
                route("/{id}") {
                    route("/tasks") {
                        post {

                            val principal = call.principal<JWTPrincipal>()!!
                            val login = principal.payload.getClaim("userId").asString()
                            val groupId = call.parameters.getOrFail("id").toInt()
                            val taskIds = call.receive<List<Int>>()
                            groupRepository.syncTasksForGroup(login, groupId, taskIds)
                            call.respond(HttpStatusCode.Created)
                        }
                    }
                    put {
                        val principal = call.principal<JWTPrincipal>()!!
                        val login = principal.payload.getClaim("userId").asString()
                        val group = call.receive<GroupDto>()
                        groupRepository.updateGroup(login, group)
                        call.respond(HttpStatusCode.Created)
                    }
                    delete {
                        val principal = call.principal<JWTPrincipal>()!!
                        val login = principal.payload.getClaim("userId").asString()
                        val groupId = call.parameters.getOrFail("id").toInt()
                        groupRepository.deleteGroup(login, groupId)
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }
}