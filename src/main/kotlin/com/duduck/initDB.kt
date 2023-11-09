package com.duduck

import com.duduck.models.Subscriptions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/your_database_name",
        driver = "org.postgresql.Driver",
        user = "your_username",
        password = "your_password"
    )
}