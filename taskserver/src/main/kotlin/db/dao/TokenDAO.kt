package com.example.db.dao

import com.example.db.tables.TokensTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TokenDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TokenDAO>(TokensTable)

    var login by TokensTable.login
    var token by TokensTable.token
}