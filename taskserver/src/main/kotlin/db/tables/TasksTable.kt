package com.example.db.tables

import com.example.dto.request.GroupDto
import com.example.dto.request.TaskDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TasksTable : IntIdTable("tasks") {
    val name = varchar("name", 255)
    val icon = integer("icon")
    val color = long("color")
    val type = varchar("type", 50)
    val note = varchar("note", 255)
    val isComplete = bool("is_complete")
    val date = long("date")
}

class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TasksTable)

    var name by TasksTable.name
    var icon by TasksTable.icon
    var color by TasksTable.color
    var type by TasksTable.type
    var note by TasksTable.note
    var isComplete by TasksTable.isComplete
    var date by TasksTable.date
}

fun taskDaoToDto(dao: TaskDAO) = TaskDto(
    id = dao.id.value,
    name = dao.name,
    icon = dao.icon,
    color = dao.color,
    type = dao.type,
    note = dao.note,
    isComplete = dao.isComplete,
    date = dao.date
)

object GroupsTable : IntIdTable("groups") {
    val name = varchar("name", 255)
    val icon = integer("icon")
    val color = long("color")
}

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
    color = dao.color
)

object GroupTaskTable : IntIdTable("group_task") {
    val groupId = reference("group_id", GroupsTable)
    val taskId = reference("task_id", TasksTable)
}

class GroupTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GroupTaskDAO>(GroupTaskTable)

    var groupId by GroupDAO referencedOn GroupTaskTable.groupId
    var taskId by TaskDAO referencedOn GroupTaskTable.taskId
}

object UserTaskTable : IntIdTable("user_task") {
    val login = reference("login", UsersTable)
    val taskId = reference("task_id", TasksTable)
}

class UserTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTaskDAO>(UserTaskTable)

    var login by UserDAO referencedOn UserTaskTable.login
    var taskId by TaskDAO referencedOn UserTaskTable.taskId
}

object UserGroupTable : IntIdTable("user_group") {
    val login = reference("login", UsersTable)
    val groupId = reference("group_id", GroupsTable)
}

class UserGroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGroupDAO>(UserGroupTable)

    var login by UserDAO referencedOn UserGroupTable.login
    var groupId by GroupDAO referencedOn UserGroupTable.groupId
}