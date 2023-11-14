package com.duduck.routes

import com.duduck.models.Subscriptions
import com.duduck.models.User
import com.duduck.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Route.userRouting() {
    route("/user") {
        get {
            val users = transaction {
                Users.selectAll().map { Users.toUsers(it) }
            }

            return@get call.respond(users)
        }

        get("id") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "User not found!",
                status = HttpStatusCode.NotFound
            )

            val users: List<User> = transaction {
                Users.select { Users.id eq id }.map { Users.toUsers(it) }
            }

            if (users.isNotEmpty()) {
                return@get call.respond(users.first())
            }
            return@get call.respondText("User not found!")
        }

        post {
            val user = call.receive<User>()

            user.id = UUID.randomUUID().toString()
            transaction {
                Users.insert {
                    it[id] = user.id!!
                    it[email] = user.email
                    it[password] = user.password
                }
            }
        }

        delete("id") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Insert a valid id!",
                status = HttpStatusCode.BadRequest
            )
            val delete: Int = transaction {
                Users.deleteWhere { Users.id eq id }
            }
            if (delete == 1) {
                return@delete call.respondText("Deleted!", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Subscription not found!", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerUserRoutes() {
    routing {
        userRouting()
    }
}
