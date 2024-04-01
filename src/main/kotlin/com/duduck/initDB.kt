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
        "jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?user=postgres.mmivfgemdqlobacwepav&password=@Jrfd1611Duduck",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "@Jrfd1611Duduck"
    )


    transaction {
        SchemaUtils.create(Subscriptions)
        SchemaUtils.create(UserSubscriptions)
        SchemaUtils.create(Users)
        SchemaUtils.create(Cards)
    }
}