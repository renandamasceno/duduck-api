package com.duduck.routes

import com.auth0.jwt.JWT
import com.duduck.jwtConfig.JwtConfig
import com.duduck.models.UserLogin
import com.duduck.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.loginRouting() {
    route("/login") {
        post {
            val credentials = call.receive<UserLogin>()

            withContext(Dispatchers.IO) {
                val user = transaction {
                    Users.select { Users.email eq credentials.email }.firstNotNullOfOrNull { Users.toUsers(it) }
                }
                if (user != null && user.password == credentials.password) {
                    val token = JWT.create()
                        .withSubject("Authentication")
                        .withIssuer(JwtConfig.issuer)
                        .withClaim("email", user.email)
                        .withClaim("password", user.password)
                        .sign(JwtConfig.algorithm)
                    call.respond(
                        mapOf(
                            "token" to token,
                            "id" to user.id
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Credenciais inválidas")
                }
            }
        }
    }
}

fun Application.loginRoutes() {
    routing {
        loginRouting()
    }
}
