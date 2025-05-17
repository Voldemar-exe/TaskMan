package com.example.routing

import com.example.db.repository.GroupRepositoryImpl
import com.example.db.repository.TaskRepositoryImpl
import com.example.db.repository.UserRepositoryImpl
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing

// TODO REDUCE CODE, CREATE SERVICE FOR TASK AND GROUP
fun Application.configureDataRouting() {
    val taskRepository = TaskRepositoryImpl()
    val groupRepository = GroupRepositoryImpl()
    val userRepository = UserRepositoryImpl()

    routing {
        authenticate("auth-jwt") {
            userRoute(userRepository)
            taskRoute(taskRepository)
            groupRoute(groupRepository)
        }
    }
}
