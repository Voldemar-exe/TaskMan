package com.example.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UserGroupTable : IntIdTable("user_group") {
    val login = reference("login", UsersTable)
    val groupId = reference("group_id", GroupsTable)
}