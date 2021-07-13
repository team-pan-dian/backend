package com.hack

import com.auth0.jwt.JWT
import com.hack.db.initDB
import com.hack.loginSystem.JWTConfig
import com.hack.loginSystem.login
import com.hack.videoSystem.video
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
//xigua too dian
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    initDB()
    install(Sessions) {
    }

    install(Authentication) {
        jwt {
            realm = "ktor.io"
            verifier(JWTConfig.verifier)

        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        login()
        video()
        authenticate {

        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!")
        }
    }
}

