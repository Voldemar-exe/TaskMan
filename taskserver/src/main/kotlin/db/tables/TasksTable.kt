package com.example.db.tables

import com.example.dto.request.TaskDto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

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
    val tasks by TaskDAO via GroupTaskTable
}

object GroupTaskTable : Table("group_task") {
    val groupId = integer("group_id").references(GroupsTable.id)
    val taskId = integer("task_id").references(TasksTable.id)
    override val primaryKey = PrimaryKey(groupId, taskId)
}

object UserTaskTable : IntIdTable("user_task") {
    val user = reference("user", UsersTable)
    val task = reference("task", TasksTable)
}

class UserTaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTaskDAO>(UserTaskTable)

    var user by UserDAO referencedOn UserTaskTable.user
    var task by TaskDAO referencedOn UserTaskTable.task
}

object UserGroupTable : IntIdTable("user_group") {
    val user = reference("user", UsersTable)
    val group = reference("task", GroupsTable)
}

class UserGroupDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTaskDAO>(UserTaskTable)

    var user by UserDAO referencedOn UserGroupTable.user
    var group by GroupDAO referencedOn UserGroupTable.group
}