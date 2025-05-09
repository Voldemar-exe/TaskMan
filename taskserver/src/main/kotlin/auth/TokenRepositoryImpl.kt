package com.example.auth

import com.example.db.TokenRepository
import com.example.db.tables.TokensTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class TokenRepositoryImpl : TokenRepository {
    override fun saveToken(login: String, token: String) {
        transaction {
            TokensTable.insert {
                it[TokensTable.login] = login
                it[TokensTable.token] = token
            }
        }
    }

    override fun validateToken(token: String): Boolean {
        return transaction {
            TokensTable.selectAll().where { TokensTable.token eq token }.count() > 0
        }
    }
}