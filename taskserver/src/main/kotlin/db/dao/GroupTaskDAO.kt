package com.example.db.dao

import com.example.db.tables.GroupTaskTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GroupTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupTaskDAO>(GroupTaskTable)

    var groupId by GroupDAO referencedOn GroupTaskTable.groupId
    var taskId by TaskDAO referencedOn GroupTaskTable.taskId
}