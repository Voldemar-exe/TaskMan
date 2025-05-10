package com.example.auth

import com.example.db.TokenRepository
import com.example.db.UserRepository
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

interface AuthService {
    suspend fun register(credentials: RegisterReceiveRemote): Result<LoginResponseRemote>
    suspend fun findByLogin(login: String): Result<UserDto>
    suspend fun deleteByLogin(login: String): Result<Boolean>
    suspend fun login(credentials: LoginReceiveRemote): Result<LoginResponseRemote>
}

class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : AuthService {
    override suspend fun register(
        user: RegisterReceiveRemote
    ): Result<LoginResponseRemote> {
        return userRepository.findByLogin(user.login)?.let {
            Result.failure(IllegalArgumentException("User exists"))
        } ?: run {
            val passwordHash = BCrypt.hashpw(user.password, BCrypt.gensalt())
            userRepository.createUser(
                UserDto(
                    user.login,
                    passwordHash,
                    user.username,
                    user.email
                )
            )
            val token = JwtConfig.generateToken(user.login)
            tokenRepository.saveToken(user.login, token)
            Result.success(LoginResponseRemote(token))
        }
    }

    override suspend fun findByLogin(login: String): Result<UserDto> {
        return userRepository.findByLogin(login)?.let {
            Result.success(it)
        } ?: run {
            Result.failure(IllegalArgumentException("User not exist"))
        }
    }

    override suspend fun deleteByLogin(login: String): Result<Boolean> {
        return when (userRepository.deleteByLogin(login)) {
            true -> Result.success(true)
            false -> Result.failure(IllegalArgumentException("User not exist"))
        }
    }

    override suspend fun login(
        credentials: LoginReceiveRemote
    ): Result<LoginResponseRemote> {
        return userRepository.findByLogin(credentials.login)?.let { user ->
            if (BCrypt.checkpw(credentials.password, user.passwordHash)) {
                val token = JwtConfig.generateToken(credentials.login)
                tokenRepository.saveToken(credentials.login, token)
                Result.success(LoginResponseRemote(token))
            } else {
                Result.failure(IllegalArgumentException("Invalid credentials"))
            }
        } ?: Result.failure(IllegalArgumentException("User not found"))
    }

}

@Serializable
data class UserDto(
    val login: String,
    val passwordHash: String,
    val username: String?,
    val email: String?
)