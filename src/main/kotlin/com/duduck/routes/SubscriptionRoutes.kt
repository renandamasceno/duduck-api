package com.duduck.routes

import com.duduck.models.Subscription
import com.duduck.models.Subscriptions
import com.duduck.models.UserSubscriptions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

        get("{id}") {
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

        put("{id}") {
            val id = call.parameters["id"] ?: return@put call.respondText(
                "Insert a valid id!",
                status = HttpStatusCode.BadRequest
            )
            val updatedSubscription = call.receive<Subscription>()

            val updatedRowCount = transaction {
                Subscriptions.update({ Subscriptions.id eq id }) {
                    it[name] = updatedSubscription.name!!
                    it[image] = updatedSubscription.image!!
                    it[price] = updatedSubscription.price!!
                    it[description] = updatedSubscription.description!!
                }
            }

            if (updatedRowCount == 1) {
                return@put call.respondText("Subscription with ID $id updated!")
            }
            return@put call.respondText("Subscription with ID $id not found!")
        }

        patch("{id}") {
            val id = call.parameters["id"] ?: return@patch call.respondText(
                "Insert a valid id!",
                status = HttpStatusCode.BadRequest
            )

            val existingSubscription = transaction {
                Subscriptions.select { (Subscriptions.id eq id) }
                    .map { Subscriptions.toSubscription(it) }
                    .firstOrNull()
            }

            if (existingSubscription != null) {
                val partialSubscrition = call.receive<Subscription>()

                transaction {
                    Subscriptions.update({ Subscriptions.id eq id }) {
                        partialSubscrition.name?.let { name -> it[Subscriptions.name] = name }
                        partialSubscrition.image?.let { image -> it[Subscriptions.image] = image }
                        partialSubscrition.price?.let { price -> it[Subscriptions.price] = price }
                        partialSubscrition.description?.let { description ->
                            it[Subscriptions.description] = description
                        }
                    }
                }
                return@patch call.respond(HttpStatusCode.OK, "Subscritption partial updated!")
            }
            call.respond(HttpStatusCode.NotFound, "Subscription not found!")

        }

        delete("{id}") {
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

    route("/subscriptions/{usersId}") {
        get("/subscriptionList") {
            val userID = call.parameters["usersId"] ?: return@get call.respondText(
                "UserId not found!!",
                status = HttpStatusCode.NotFound
            )

            val userSubscriptions = transaction {
                (UserSubscriptions innerJoin Subscriptions)
                    .select { UserSubscriptions.userId eq userID }
                    .map { Subscriptions.toSubscription(it) }
            }

            if (userSubscriptions.isNotEmpty()) {
                return@get call.respond(userSubscriptions)
            }
            return@get call.respond(HttpStatusCode.NotFound, "No subscriptions found for user $userID!")
        }

        post("{subscriptionId}") {
            val userID = call.parameters["usersId"] ?: return@post call.respondText(
                "UserId not found!!",
                status = HttpStatusCode.NotFound
            )

            val subscriptionId = call.parameters["subscriptionId"] ?: return@post call.respondText(
                "SubscriptionId not found!",
                status = HttpStatusCode.NotFound
            )
            val existingSubscription = transaction {
                Subscriptions.select { Subscriptions.id eq subscriptionId }
                    .map { Subscriptions.toSubscription(it) }
                    .firstOrNull()
            }

            if (existingSubscription != null) {
                transaction {
                    UserSubscriptions.insert {
                        it[UserSubscriptions.userId] = userID
                        it[UserSubscriptions.subscriptionId] = subscriptionId
                    }
                }
                return@post call.respondText(
                    "User associated with existing subscription!",
                    status = HttpStatusCode.Created
                )
            }
            return@post call.respondText(
                "Subscription not found!",
                status = HttpStatusCode.NotFound
            )
        }

        delete("{subscriptionId}") {
            val userID = call.parameters["usersId"] ?: return@delete call.respondText(
                "Usuário não encontrado!",
                status = HttpStatusCode.NotFound
            )

            val subscriptionId = call.parameters["subscriptionId"] ?: return@delete call.respondText(
                "ID da assinatura não encontrado!",
                status = HttpStatusCode.NotFound
            )

            val userSubscription = transaction {
                UserSubscriptions.select {
                    (UserSubscriptions.userId eq userID) and (UserSubscriptions.subscriptionId eq subscriptionId)
                }.firstOrNull()
            }

            if (userSubscription != null) {
                transaction {
                    UserSubscriptions.deleteWhere {
                        (UserSubscriptions.userId eq userID) and (UserSubscriptions.subscriptionId eq subscriptionId)
                    }
                }
                return@delete call.respondText(
                    "Assinatura removida com sucesso!",
                    status = HttpStatusCode.OK
                )
            }
            return@delete call.respondText(
                "Assinatura não encontrada para o usuário!",
                status = HttpStatusCode.NotFound
            )
        }
    }

}

fun Application.registerSubscriptionRoutes() {
    routing {
        subscriptionRouting()
    }
}