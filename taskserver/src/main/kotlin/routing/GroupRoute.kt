package com.example.routing

import com.example.db.repository.GroupRepository
import com.example.shared.dto.GroupDto
import com.example.shared.response.ServerResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail

fun Route.groupRoute(groupRepository: GroupRepository) {
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
            val group = call.receive<GroupDto>()
            groupRepository.createGroup(login, group)?.let {
                groupRepository.syncTasksForGroup(
                    login,
                    it,
                    group.tasks.map { it.id }
                )
                call.respond(it)
            }
        }
        route("/{id}") {
            route("/tasks") {
                post {
                    val principal = call.principal<JWTPrincipal>()!!
                    val login = principal.payload.getClaim("userId").asString()
                    val groupId = call.parameters.getOrFail("id").toInt()
                    val taskIds = call.receive<List<Int>>()
                    val result = groupRepository.syncTasksForGroup(login, groupId, taskIds)
                    result.fold(
                        onSuccess = { call.respond(HttpStatusCode.Created, it) },
                        onFailure = {
                            call.respond(HttpStatusCode.NotFound, it.message ?: "Failed")
                        }
                    )
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
                val result = groupRepository.deleteGroup(login, groupId)
                result.fold(
                    onSuccess = { call.respond(HttpStatusCode.OK, it) },
                    onFailure = { call.respond(HttpStatusCode.NotFound, it.message ?: "Failed") }
                )
            }
        }
    }
}
