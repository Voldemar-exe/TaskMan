package com.example

import com.example.auth.configureAuthRouting
import com.example.auth.configureSecurity
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    startKoin {
        modules(authModule)
    }

    configureSerialization()
    configureDatabases()
    configureTemplating()
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureFrameworks()
    configureRouting()
    configureAuthRouting()
}
