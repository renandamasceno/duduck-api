package com.duduck.models

import com.duduck.models.Cards.userId
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


object Users : Table() {
    val id: Column<String> = char("id", 36)
    val email: Column<String> = varchar("email", 56)
    val password: Column<String> = varchar("password", 32)

    override val primaryKey = PrimaryKey(id, name = "PK_Users_ID")

    fun toUsers(row: ResultRow): User = transaction {
        val cards = Cards.select { userId eq row[Users.id] }
            .map { Cards.toCards(it) }
        val subscriptions = Subscriptions.innerJoin(UserSubscriptions)
            .select { UserSubscriptions.userId eq row[Users.id] }
            .map { Subscriptions.toSubscription(it) }

        User(
            id = row[Users.id],
            email = row[email],
            password = row[password],
            cards = cards,
            subscriptions = subscriptions
        )
    }

}

@Serializable
data class User(
    var id: String? = null,
    val email: String? = null,
    val password: String? = null,
    val cards: List<Card> = emptyList(),
    val subscriptions: List<Subscription> = emptyList()
)
