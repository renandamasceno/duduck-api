package com.duduck.routes

import com.duduck.models.User
import com.duduck.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.userRouting() {
    route("/user") {
        get {
            val users = transaction {
                Users.selectAll().map { Users.toUsers(it) }
            }

            return@get call.respond(users)
        }

        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "User not found!",
                status = HttpStatusCode.NotFound
            )

            val user: List<User> = transaction {
                Users.select { Users.id eq id }.map { Users.toUsers(it) }
            }

            if (user.isNotEmpty()) {
                return@get call.respond(user.first())
            }
            return@get call.respondText("User not found!")
        }

        post {
            val user = call.receive<User>()

            user.id = UUID.randomUUID().toString()
            transaction {
                Users.insert {
                    it[id] = user.id!!
                    it[email] = user.email!!
                    it[password] = user.password!!
                }
            }
            return@post call.respondText("New User created!", status = HttpStatusCode.Created)
        }

        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "User not found!",
                status = HttpStatusCode.NotFound
            )

            val existingUser = transaction {
                Users.select { Users.id eq id }.map { Users.toUsers(it) }.firstOrNull()
            }

            if (existingUser != null) {
                val updatedUser = call.receive<User>()
                transaction {
                    Users.update({ Users.id eq id }) {
                        it[email] = updatedUser.email!!
                        it[password] = updatedUser.password!!
                    }
                }
                return@put call.respondText("Updated!", status = HttpStatusCode.OK)
            }
            return@put call.respondText("Id not found!", status = HttpStatusCode.NotFound)
        }


        patch("{id}") {
            val id = call.parameters["id"] ?: return@patch call.respond(HttpStatusCode.NotFound, "User not found!!")

            val existingUser = transaction {
                Users.select { Users.id eq id }.map { Users.toUsers(it) }.firstOrNull()
            }

            if (existingUser != null) {
                val partialUser = call.receive<User>()

                transaction {
                    Users.update({ Users.id eq id }) {
                        partialUser.email?.let { email -> it[Users.email] = email }
                        partialUser.password?.let { password -> it[Users.password] = password }
                    }
                }

                call.respond(HttpStatusCode.OK, "User updated (partial)!")
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found!")
            }
        }

        delete("{id}") {
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
            return@delete call.respondText("Id not found!", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerUserRoutes() {
    routing {
        userRouting()
    }
}
