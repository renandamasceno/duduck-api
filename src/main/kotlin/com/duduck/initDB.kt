package com.duduck

import com.duduck.models.Subscriptions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/duduck",
        driver = "org.postgresql.Driver",
        user = "renandamasceno",
        password = "@Jrfd1611"
    )

    transaction {
        SchemaUtils.create(Subscriptions)
    }
}