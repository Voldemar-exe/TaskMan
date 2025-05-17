package com.example.auth

import com.example.db.repository.GroupRepository
import com.example.db.repository.TaskRepository
import com.example.db.repository.TokenRepository
import com.example.db.repository.UserRepository
import com.example.shared.dto.UserDto
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.shared.response.LoginResponse
import com.example.shared.response.RegisterResponse
import org.mindrot.jbcrypt.BCrypt

class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val groupRepository: GroupRepository,
    private val taskRepository: TaskRepository
) : AuthService {
    override suspend fun register(
        request: RegisterRequest
    ): Result<RegisterResponse> {
        return userRepository.findByLogin(request.login)?.let {
            Result.failure(IllegalArgumentException("User exists"))
        } ?: run {
            val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
            val user = userRepository.createUser(
                UserDto(
                    request.login,
                    passwordHash,
                    request.username,
                    request.email
                )
            )
            val token = JwtConfig.generateToken(request.login)
            tokenRepository.saveToken(request.login, token)
            try {
                val tasks = request.tasksWithoutGroup.map {
                    it.copy(id = taskRepository.createTask(user.login.value, it)!!)
                }
                val groups = request.groupsWithTasks.map {
                    it.copy(
                        id = groupRepository.createGroup(user.login.value, it)!!,
                        tasks = it.tasks.map {
                            it.copy(id = taskRepository.createTask(user.login.value, it)!!)
                        }
                    )
                }
                groups.forEach {
                    groupRepository.syncTasksForGroup(
                        user.login.value,
                        it.id,
                        it.tasks.map { it.id }
                    )
                }
                Result.success(RegisterResponse(token, tasks, groups))
            } catch (e: Exception) {
                return Result.failure(
                    IllegalArgumentException("Create User. Can't put data. Err: ${e.message}")
                )
            }
        }
    }

    override suspend fun findByLogin(login: String): Result<UserDto> {
        return userRepository.findByLogin(login)?.let {
            Result.success(it)
        } ?: run {
            Result.failure(IllegalArgumentException("User not exist"))
        }
    }

    override suspend fun login(
        request: LoginRequest
    ): Result<LoginResponse> {
        return userRepository.findByLogin(request.login)?.let { user ->
            if (BCrypt.checkpw(request.password, user.passwordHash)) {
                val token = JwtConfig.generateToken(request.login)
                tokenRepository.saveToken(request.login, token)
                val tasks = taskRepository.getTasksWithoutGroupForUser(user.login)
                val groups = groupRepository.getGroupsForUser(user.login).map {
                    it.copy(tasks = groupRepository.getGroupTasks(user.login, it.id))
                }
                Result.success(LoginResponse(token, tasks, groups))
            } else {
                Result.failure(IllegalArgumentException("Invalid credentials"))
            }
        } ?: Result.failure(IllegalArgumentException("User not found"))
    }

}

