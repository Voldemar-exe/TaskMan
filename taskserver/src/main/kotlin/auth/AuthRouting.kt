package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.cache.InMemoryCache
import com.example.cache.TokenCache
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.configureAuthRouting() {
    routing {
        post("/login") {
            val credentials = call.receive<LoginReceiveRemote>()
            val user = InMemoryCache.userList.find {
                it.login == credentials.login && it.password == credentials.password
            }
            if (user == null) {
                call.respondText("Invalid credentials")
            } else {
                val token = createToken(user.login)
                call.respondText(LoginResponseRemote(token).toString())
            }
        }
        post("/register") {
            val user = call.receive<RegisterReceiveRemote>()
            if (InMemoryCache.userList.any { it.login == user.login }) {
                call.respondText("User exists")
            } else {
                val token = createToken(user.login)
                InMemoryCache.userList.add(user)
                InMemoryCache.token.add(TokenCache(user.login, token))
                call.respondText(RegisterResponseRemote(token).toString())
            }
        }
    }
}

private fun createToken(login: String): String {
    return JWT.create()
        .withAudience("jwt-audience")
        .withIssuer("your-issuer")
        .withClaim("login", login)
        .sign(Algorithm.HMAC256("secret"))
}