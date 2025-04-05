package com.example

import com.example.auth.configureAuthRouting
import com.example.auth.configureSecurity
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
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
