package com.example.db.dao

import com.example.db.tables.UserTaskTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTaskDAO>(UserTaskTable)

    var login by UserDAO.Companion referencedOn UserTaskTable.login
    var taskId by TaskDAO referencedOn UserTaskTable.taskId
}