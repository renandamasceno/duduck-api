package com.duduck.plugins

import com.duduck.routes.loginRoutes
import com.duduck.routes.registerCardRoutes
import com.duduck.routes.registerSubscriptionRoutes
import com.duduck.routes.registerUserRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    registerSubscriptionRoutes()
    registerUserRoutes()
    registerCardRoutes()
    loginRoutes()
}
