package com.duduck.routes

import com.duduck.models.Card
import com.duduck.models.Cards
import com.duduck.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.cardRouting() {
    route("/cards") {
        get {
            val cards = transaction {
                Cards.selectAll().map { Users.toUsers(it) }
            }

            return@get call.respond(cards)
        }

        get("id") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Card not found!",
                status = HttpStatusCode.NotFound
            )

            val cards: List<Card> = transaction {
                Cards.select { Cards.id eq id }.map { Cards.toCards(it) }
            }

            if (cards.isNotEmpty()) {
                return@get call.respond(cards.first())
            }
            return@get call.respondText("Card not found!")

        }

        post {
            val card = call.receive<Card>()

            card.id = UUID.randomUUID().toString()

            transaction {
                Cards.insert {
                    it[id] = card.id!!
                    it[nameUser] = card.nameUser
                    it[number] = card.number
                    it[expirationDate] = card.expirationDate
                    it[cardIssuer] = card.cardIssuer
                }
            }
        }
    }

}

fun Application.registerCardRoutes() {
    routing {
        cardRouting()
    }
}
