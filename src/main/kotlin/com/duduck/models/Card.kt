package com.duduck.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Cards : Table() {
    val id: Column<String> = char("id", 36)
    val nameUser: Column<String> = varchar("nameUSer", 56)
    val number: Column<String> = varchar("number", 56)
    val expirationDate: Column<String> = varchar("expirationDate", 36)
    val cardIssuer: Column<String> = varchar("cardIssuer", 36)
    val userId: Column<String> = char("user_id", 36) references Users.id

    override val primaryKey = PrimaryKey(id, name = "PK_Cards_Id")

    fun toCards(row: ResultRow): Card = Card(
        id = row[id],
        nameUser = row[nameUser],
        number = row[number],
        expirationDate = row[expirationDate],
        cardIssuer = row[cardIssuer],
        userId = row[userId]
    )
}

@Serializable
data class Card(
    var id: String? = null,
    val userId: String? = null,
    val nameUser: String? = null,
    val number: String? = null,
    val expirationDate: String? = null,
    val cardIssuer: String? = null
)
