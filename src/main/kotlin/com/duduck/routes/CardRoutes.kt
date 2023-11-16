package com.duduck.routes

import com.duduck.models.Card
import com.duduck.models.Cards
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Route.cardRouting() {
    route("/{usersId}/cards") {
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
                    it[nameUser] = card.nameUser
                    it[number] = card.number
                    it[expirationDate] = card.expirationDate
                    it[cardIssuer] = card.cardIssuer
                }
            }
            return@post call.respondText("Card created for user $userID!", status = HttpStatusCode.Created)
        }
    }

}

fun Application.registerCardRoutes() {
    routing {
        cardRouting()
    }
}
