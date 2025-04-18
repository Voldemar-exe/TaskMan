package com.example

import com.example.db.DatabaseFactory
import io.ktor.server.application.Application

fun Application.configureDatabases() {
    DatabaseFactory.init(this)
}
