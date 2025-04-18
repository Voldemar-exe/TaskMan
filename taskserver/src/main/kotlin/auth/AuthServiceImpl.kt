package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.TokenRepository
import com.example.db.UserRepository
import org.mindrot.jbcrypt.BCrypt

interface AuthService {
    suspend fun register(credentials: RegisterReceiveRemote): Result<LoginResponseRemote>
    suspend fun deleteByLogin(login: String): Result<DeleteResponse>
    suspend fun login(credentials: LoginReceiveRemote): Result<LoginResponseRemote>
}

class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : AuthService {
    override suspend fun register(
        credentials: RegisterReceiveRemote
    ): Result<LoginResponseRemote> {
        return userRepository.findByLogin(credentials.login)?.let {
            Result.failure(IllegalArgumentException("User exists"))
        } ?: run {
            val passwordHash = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            userRepository.createUser(
                credentials.login,
                passwordHash,
                credentials.username,
                credentials.email
            )
            val token = createToken(credentials.login)
            tokenRepository.saveToken(credentials.login, token)
            Result.success(LoginResponseRemote(token))
        }
    }

    override suspend fun deleteByLogin(login: String): Result<DeleteResponse> {
        return userRepository.deleteByLogin(login)?.let {
            Result.failure(IllegalArgumentException("User not exist"))
        } ?: run {
            Result.success(DeleteResponse(userRepository.deleteByLogin(login) == true))
        }
    }

    override suspend fun login(
        credentials: LoginReceiveRemote
    ): Result<LoginResponseRemote> {
        return userRepository.findByLogin(credentials.login)?.let { user ->
            if (BCrypt.checkpw(credentials.password, user.passwordHash)) {
                val token = createToken(credentials.login)
                tokenRepository.saveToken(credentials.login, token)
                Result.success(LoginResponseRemote(token))
            } else {
                Result.failure(IllegalArgumentException("Invalid credentials"))
            }
        } ?: Result.failure(IllegalArgumentException("User not found"))
    }

}

data class User(
    val login: String,
    val passwordHash: String,
    val username: String,
    val email: String
)

private fun createToken(login: String): String {
    return JWT.create()
        .withAudience("jwt-audience")
        .withIssuer("your-issuer")
        .withClaim("login", login)
        .sign(Algorithm.HMAC256("secret"))
}