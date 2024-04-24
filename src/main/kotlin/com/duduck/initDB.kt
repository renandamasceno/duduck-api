package com.duduck

import com.duduck.models.Cards
import com.duduck.models.Subscriptions
import com.duduck.models.UserSubscriptions
import com.duduck.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB() {
    Database.connect(
        url = System.getenv("DATABASE_URL"),
        driver = System.getenv("DATABASE_DRIVER"),
        user = System.getenv("DATABASE_USER"),
        password = System.getenv("DATABASE_PASSWORD")
    )


    transaction {
        SchemaUtils.create(Subscriptions)
        SchemaUtils.create(UserSubscriptions)
        SchemaUtils.create(Users)
        SchemaUtils.create(Cards)
    }
}