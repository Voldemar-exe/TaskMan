package com.example.db.dao

import com.example.db.tables.UserGroupTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserGroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGroupDAO>(UserGroupTable)

    var login by UserDAO.Companion referencedOn UserGroupTable.login
    var groupId by GroupDAO referencedOn UserGroupTable.groupId
}