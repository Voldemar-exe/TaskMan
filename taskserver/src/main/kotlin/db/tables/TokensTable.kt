package com.example.db.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object TokensTable : Table("tokens") {
    val tokenId = integer("token_id").autoIncrement()
    val login = varchar("login", 50).references(
        UsersTable.login,
        onDelete = ReferenceOption.CASCADE
    )
    val token = varchar("token", 255)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(tokenId)
}