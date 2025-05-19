package com.example.routing

import com.example.db.repository.SyncRepository
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import com.example.shared.request.SyncRequest
import com.example.shared.response.SyncResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.syncRoute(syncRepository: SyncRepository) {
    route("/sync") {
        post("/tasks") {
            val principal = call.principal<JWTPrincipal>()!!
            val login = principal.payload.getClaim("userId").asString()
            val request = call.receive<SyncRequest<TaskDto>>()
            val result = syncRepository.syncTasks(
                login,
                request.entitiesToUpdate,
                request.allEntitiesIds
            )

            result.fold(
                onSuccess = {
                    call.respond(HttpStatusCode.OK, SyncResponse(it))
                },
                onFailure = {
                    call.respond(HttpStatusCode.InternalServerError, it.message ?: "FAILED")
                }
            )
        }

        post("/groups") {
            val principal = call.principal<JWTPrincipal>()!!
            val login = principal.payload.getClaim("userId").asString()
            val request = call.receive<SyncRequest<GroupDto>>()
            val result = syncRepository.syncGroups(
                login,
                request.entitiesToUpdate,
                request.allEntitiesIds
            )

            result.fold(
                onSuccess = {
                    call.respond(HttpStatusCode.OK, SyncResponse(it))
                },
                onFailure = {
                    call.respond(HttpStatusCode.InternalServerError, it.message ?: "FAILED")
                }
            )
        }
    }
}
