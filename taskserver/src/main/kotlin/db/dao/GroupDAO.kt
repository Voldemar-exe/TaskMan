package com.example.db.dao

import com.example.db.tables.GroupsTable
import com.example.shared.dto.GroupDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupDAO>(GroupsTable)

    var name by GroupsTable.name
    var icon by GroupsTable.icon
    var color by GroupsTable.color
}

fun groupDaoTpDto(dao: GroupDAO) = GroupDto(
    id = dao.id.value,
    name = dao.name,
    icon = dao.icon,
    color = dao.color,
    tasks = emptyList()
)