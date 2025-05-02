package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object TasksTable : IntIdTable("tasks") {
    val taskId = integer("task_id")
    val name = varchar("name", 255)
    val icon = integer("icon")
    val color = long("color")
    val type = varchar("type", 50)
    val note = varchar("note", 255)
    val isComplete = bool("is_complete")
    val date = long("date")
}

object GroupsTable : IntIdTable("groups") {
    val groupId = integer("group_id").autoIncrement()
    val name = varchar("name", 255)
    val icon = integer("icon")
    val color = long("color")
}

object GroupTaskTable : Table("group_task") {
    val groupId = integer("group_id").references(GroupsTable.groupId)
    val taskId = integer("task_id").references(TasksTable.taskId)
    override val primaryKey = PrimaryKey(groupId, taskId)
}

object UserTaskTable : Table("user_task") {
    val login = varchar("login", 50).references(UsersTable.login)
    val taskId = integer("task_id").references(TasksTable.taskId)
    override val primaryKey = PrimaryKey(login, taskId)
}

object UserGroupTable : Table("user_group") {
    val login = varchar("login", 50).references(UsersTable.login)
    val groupId = integer("group_id").references(GroupsTable.groupId)
    override val primaryKey = PrimaryKey(login, groupId)
}