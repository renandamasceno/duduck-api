package com.duduck.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


object Users : Table() {
    val id: Column<String> = char("id", 36)
    val email: Column<String> = varchar("email", 56)
    val password: Column<String> = varchar("password", 32)

    override val primaryKey = PrimaryKey(id, name = "PK_Users_ID")

    fun toUsers(row: ResultRow): User = User(
        id = row[id],
        email = row[email],
        password = row[password]
    )
}

@Serializable
data class User(
    var id: String? = null,
    val email: String,
    val password: String
)
