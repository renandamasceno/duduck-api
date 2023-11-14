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

    override val primaryKey = PrimaryKey(id, name = "PK_Cards_Id")

    fun toCards(row: ResultRow): Card = Card(
        id = row[id],
        nameUser = row[nameUser],
        number = row[number],
        expirationDate = row[expirationDate],
        cardIssuer = row[cardIssuer]
    )
}

@Serializable
data class Card(
    var id: String? = null,
    val nameUser: String,
    val number: String,
    val expirationDate: String,
    val cardIssuer: String = ""
)
