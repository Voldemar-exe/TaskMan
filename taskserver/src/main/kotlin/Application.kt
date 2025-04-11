package com.example

import com.example.auth.configureAuthRouting
import com.example.auth.configureSecurity
import io.ktor.server.application.Application
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
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
