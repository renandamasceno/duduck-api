package com.duduck.jwtConfig

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.duduck.models.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.sql.Date

object JwtConfig {
    private const val secret = "duduck-secret"
    const val issuer = "com.duduck"
    val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

}

fun Application.configureAuthentication() {
    authentication {
        jwt {
            verifier(JwtConfig.verifier)
            realm = "com.duduck"
            validate {
                val email = it.payload.getClaim("email").asString()
                val password = it.payload.getClaim("password").asString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    JWTPrincipal(it.payload)
                } else {
                    null
                }
            }
        }
    }
}