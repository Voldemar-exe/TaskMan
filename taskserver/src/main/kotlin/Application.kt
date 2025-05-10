package com.example

import com.example.auth.configureSecurity
import com.example.di.authModule
import com.example.plugins.configureHTTP
import com.example.plugins.configureMonitoring
import com.example.plugins.configureSerialization
import com.example.routing.configureAuthRouting
import com.example.routing.configureDataRouting
import com.example.routing.configureDatabases
import com.example.routing.configureFrameworks
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
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureFrameworks()
    configureAuthRouting()
    configureDataRouting()
}
