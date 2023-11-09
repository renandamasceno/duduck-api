package com.duduck.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


object Subscriptions : Table() {
    val id: Column<Int> = integer("id")
    val name: Column<String> = varchar("name", 50)
    val image: Column<String> = varchar("image", 144)
    val price: Column<Double> = double("price")
    val description: Column<String> = varchar("description", 512)

    override val primaryKey = PrimaryKey(id, name = "PK_Subscriptions_ID")

    fun toSubscription(row: ResultRow): Subscription = Subscription(
        id = row[Subscriptions.id],
        name = row[Subscriptions.name],
        image = row[Subscriptions.image],
        price = row[Subscriptions.price],
        description = row[Subscriptions.description]
    )
}

@Serializable
data class Subscription(
    var id: Int,
    var name: String?,
    var image: String? = null,
    var price: Double?,
    var description: String? = null
)
