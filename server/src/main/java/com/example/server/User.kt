package com.example.server

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import org.ktorm.schema.int
import org.ktorm.schema.varchar

data class User(
    val id: Int? = null,
    val username: String,
    val email: String? = null,
    val passwordHash: String
)

object Users : BaseTable<User>("users") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val email = varchar("email")
    val passwordHash = varchar("password_hash")

    override fun doCreateEntity(
        row: QueryRowSet,
        withReferences: Boolean
    ) = User(
        id = row[id] ?: 0,
        username = row[username] ?: "",
        email = row[email] ?: "",
        passwordHash = row[passwordHash] ?: ""
    )
}
