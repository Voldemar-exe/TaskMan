package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object JwtConfig {
    private const val JWT_SECRET = "YOUR_SECRET_KEY"
    private const val JWT_ISSUER = "com.example"
    private const val JWT_AUDIENCE = "audience"

    private val algorithm = Algorithm.HMAC256(JWT_SECRET)

    val verifier = JWT.require(algorithm)
        .withAudience(JWT_AUDIENCE)
        .withIssuer(JWT_ISSUER)
        .build()

    fun generateToken(userId: String): String = JWT.create()
        .withAudience(JWT_AUDIENCE)
        .withIssuer(JWT_ISSUER)
        .withClaim("userId", userId)
        .sign(algorithm)

    fun debugDecode(token: String) {
        try {
            val decoded = JWT.decode(token)
            println("=== DEBUG JWT ===")
            println("Issuer   : ${decoded.issuer}")
            println("Audience : ${decoded.audience}")
            println("Claims   : ${decoded.claims.mapValues { it.value.asString() }}")
            println("=================")
        } catch (e: Exception) {
            println("Failed to decode JWT: ${e.message}")
        }
    }

}
