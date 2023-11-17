package com.duduck.routes

import com.duduck.models.Card
import com.duduck.models.Cards
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.cardRouting() {
    route("/cards/{usersId}") {
        get {
            val userID = call.parameters["usersId"] ?: return@get call.respondText(
                "UserId not found!",
                status = HttpStatusCode.NotFound
            )

            val cards = transaction {
                Cards.select { Cards.userId eq userID }
                    .map { Cards.toCards(it) }
            }
            return@get call.respond(cards)
        }

        post {
            val userID = call.parameters["usersId"] ?: return@post call.respondText(
                "UserId not found!",
                status = HttpStatusCode.NotFound
            )
            val card = call.receive<Card>()

            transaction {
                Cards.insert {
                    it[id] = card.id ?: UUID.randomUUID().toString()
                    it[userId] = userID
                    it[nameUser] = card.nameUser!!
                    it[number] = card.number!!
                    it[expirationDate] = card.expirationDate!!
                    it[cardIssuer] = card.cardIssuer!!
                }
            }
            return@post call.respondText("Card created for user $userID!", status = HttpStatusCode.Created)
        }

        put("/{cardId}") {
            val userID = call.parameters["usersId"] ?: return@put call.respondText(
                "UserId not found!!",
                status = HttpStatusCode.NotFound
            )

            val cardId = call.parameters["cardId"] ?: return@put call.respondText(
                "CardId not found!",
                status = HttpStatusCode.NotFound
            )

            val updatedCard = call.receive<Card>()

            val updatedRowCount: Int = transaction {
                Cards.update({ (Cards.userId eq userID) and (Cards.id eq cardId) }) {
                    it[nameUser] = updatedCard.nameUser!!
                    it[number] = updatedCard.number!!
                    it[expirationDate] = updatedCard.expirationDate!!
                    it[cardIssuer] = updatedCard.cardIssuer!!
                }
            }
            if (updatedRowCount > 0) {
                call.respondText("Card with ID $cardId updated for user $userID!")
            } else {
                call.respondText("Card with ID $cardId not found for user $userID", status = HttpStatusCode.NotFound)
            }
        }

        patch("/{cardId}") {
            val userID = call.parameters["usersId"] ?: return@patch call.respondText(
                "UserId not found!!",
                status = HttpStatusCode.NotFound
            )

            val cardId = call.parameters["cardId"] ?: return@patch call.respondText(
                "CardId not found!",
                status = HttpStatusCode.NotFound
            )

            val existingCard = transaction {
                Cards.select { (Cards.userId eq userID) and (Cards.id eq cardId) }
                    .map { Cards.toCards(it) }
                    .firstOrNull()
            }

            if (existingCard != null) {
                val partialCard = call.receive<Card>()

                transaction {
                    Cards.update({ (Cards.userId eq userID) and (Cards.id eq cardId) }) {
                        partialCard.nameUser?.let { nameUser -> it[Cards.nameUser] = nameUser }
                        partialCard.number?.let { number -> it[Cards.number] = number }
                        partialCard.expirationDate?.let { expirationDate -> it[Cards.expirationDate] = expirationDate }
                        partialCard.cardIssuer?.let { cardIssuer -> it[Cards.cardIssuer] = cardIssuer }
                    }
                }
                call.respond(HttpStatusCode.OK, "Card with ID $cardId for user $userID updated (partial)!")
            } else {
                call.respond(HttpStatusCode.NotFound, "Card with ID $cardId not found for user $userID")
            }
        }

        delete("/{cardId}") {
            val userID = call.parameters["usersId"] ?: return@delete call.respondText(
                "UserId not found!!",
                status = HttpStatusCode.NotFound
            )

            val cardId = call.parameters["cardId"] ?: return@delete call.respondText(
                "CardId not found!",
                status = HttpStatusCode.NotFound
            )

            val isDeleted: Int = transaction {
                Cards.deleteWhere { (userId eq userID) and (id eq cardId) }
            }

            if (isDeleted == 1) {
                return@delete call.respondText("Deleted!", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Id not found!", status = HttpStatusCode.NotFound)
        }

    }

}

fun Application.registerCardRoutes() {
    routing {
        cardRouting()
    }
}
