package com.example.db.dao

import com.example.db.tables.TasksTable
import com.example.shared.dto.TaskDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TasksTable)

    var name by TasksTable.name
    var icon by TasksTable.icon
    var color by TasksTable.color
    var type by TasksTable.type
    var isComplete by TasksTable.isComplete
    var date by TasksTable.date
}

fun taskDaoToDto(dao: TaskDAO) = TaskDto(
    id = dao.id.value,
    name = dao.name,
    icon = dao.icon,
    color = dao.color,
    type = dao.type,
    isComplete = dao.isComplete,
    date = dao.date
)

