package com.duduck.routes

import com.duduck.models.Subscription
import com.duduck.models.Subscriptions
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

fun Route.subscriptionRouting() {
    route("/subscriptions") {
        get {
            val subscriptions = transaction {
                Subscriptions.selectAll().map { Subscriptions.toSubscription(it) }
            }

            return@get call.respond(subscriptions)
        }

        get("id") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Subscription not found!",
                status = HttpStatusCode.NotFound
            )

            val subscription: List<Subscription> =
                transaction { Subscriptions.select { Subscriptions.id eq id }.map { Subscriptions.toSubscription(it) } }

            if (subscription.isNotEmpty()) {
                return@get call.respond(subscription.first())
            }
            return@get call.respondText("Subscription not found!")
        }

        post {
            val subscription = call.receive<Subscription>()

            subscription.id = UUID.randomUUID().toString()

            transaction {
                Subscriptions.insert {
                    it[id] = subscription.id!!
                    it[name] = subscription.name ?: ""
                    it[image] = subscription.image ?: ""
                    it[price] = subscription.price ?: 0.0
                    it[description] = subscription.description ?: ""
                }
            }

            call.respondText("New subscription created!", status = HttpStatusCode.Created)
        }

        delete("id") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Insert a valid id!",
                status = HttpStatusCode.BadRequest
            )
            val delete: Int = transaction {
                Subscriptions.deleteWhere { Subscriptions.id eq id }
            }
            if (delete == 1) {
                return@delete call.respondText("Deleted!", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Subscription not found!", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerSubscriptionRoutes() {
    routing {
        subscriptionRouting()
    }
}