package com.duduck.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

@Serializable
data class SubscriptionHistory(
    val id: Int,
    val userId: String,
    val subscriptionId: String,
    val action: String,
    val timestamp: Long
)

object SubsHistory : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val userId: Column<String> = varchar("userId", 36)
    val subscriptionId: Column<String> = varchar("subscriptionId", 36)
    val action: Column<String> = varchar("action", 20)
    val timestamp: Column<Long> = long("timestamp")

    override val primaryKey = PrimaryKey(id, name = "PK_History_ID")

    fun toHistory(row: ResultRow): SubscriptionHistory = SubscriptionHistory(
        id = row[id],
        userId = row[userId],
        subscriptionId = row[subscriptionId],
        action = row[action],
        timestamp = row[timestamp]
    )
}