package com.example.db.repository

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.TokenDAO
import com.example.db.tables.TokensTable
import org.jetbrains.exposed.sql.insert
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

    override suspend fun getToken(login: String): String? =
        suspendTransaction {
            TokenDAO.find { TokensTable.login eq login }.first().token
        }
}