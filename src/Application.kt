package com.hack

import com.hack.db.initDB
import com.hack.loginSystem.JWTConfig
import com.hack.loginSystem.TokenSession
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
import io.ktor.util.*

//xigua too dian
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    initDB()

    install(Sessions) {
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretAuthKey = hex("02030405060708090a0b0c")
        cookie<TokenSession>("TokenSession", SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretAuthKey))
        }
    }

    install(Authentication) {
        jwt {
            realm = "ktor.io"
            verifier(JWTConfig.verifier)
            validate {
                val id = it.payload.getClaim("id").asInt()
                if (com.hack.loginSystem.validate(id))
                    JWTPrincipal(it.payload)
                else null
            }
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

    install(ContentNegotiation) {
        gson {
        }
    }

    install(StatusPages) {
        // 404
        status(HttpStatusCode.NotFound) {
//            call.respond()
        }

        // 403 422 400
        status(
            HttpStatusCode.Forbidden,
            HttpStatusCode.ServiceUnavailable,
            HttpStatusCode.BadRequest
        ) {
//            call.respond()
        }

        // 500
        status(HttpStatusCode.InternalServerError) {
//            call.respond()
        }
    }

    routing {
        route("/api/") {
            login()
            authenticate {
                video()
            }
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!")
        }
    }
}

