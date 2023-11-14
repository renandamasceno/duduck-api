package com.duduck.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


object Subscriptions : Table() {
    val id: Column<String> = char("id",36)
    val name: Column<String> = varchar("name", 56)
    val image: Column<String> = varchar("image", 144)
    val price: Column<Double> = double("price")
    val description: Column<String> = varchar("description", 512)

    override val primaryKey = PrimaryKey(id, name = "PK_Subscriptions_Id")

    fun toSubscription(row: ResultRow): Subscription = Subscription(
        id = row[id],
        name = row[name],
        image = row[image],
        price = row[price],
        description = row[description]
    )
}

@Serializable
data class Subscription(
    var id: String? = null,
    var name: String?,
    var image: String? = null,
    var price: Double?,
    var description: String? = null
)
