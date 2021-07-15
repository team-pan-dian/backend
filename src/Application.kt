package com.hack

import com.hack.api.API
import com.hack.db.initDB
import com.hack.loginSystem.JWTConfig
import com.hack.loginSystem.User
import com.hack.loginSystem.login
import com.hack.studen.student
import com.hack.videoSystem.classSystem
import com.hack.videoSystem.video
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
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

    install(Authentication) {
        jwt {
            realm = "ktor.io"
            verifier(JWTConfig.verifier)
            validate {
                val id = it.payload.getClaim("id").asInt()
                val isTeacher = it.payload.getClaim("isTeacher").asBoolean()
                if (id != null && isTeacher != null)
                    User(id, isTeacher)
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
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(StatusPages) {
        // 404
        statusFile(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized, filePattern = "404.html")

        exception<MissingRequestParameterException> {

            call.respond(
                HttpStatusCode.UnprocessableEntity,
                API(
                    true,
                    null,
                    it.message
                )
            )
        }

        exception<BadRequestException> {
            call.respond(
                HttpStatusCode.BadRequest,
                API(
                    true,
                    null,
                    it.message
                )
            )
        }
        // 403 422 400 500
        status(
            HttpStatusCode.Forbidden,
            HttpStatusCode.ServiceUnavailable,
            HttpStatusCode.BadRequest,
            HttpStatusCode.UnprocessableEntity,
            HttpStatusCode.InternalServerError
        ) {
            call.respond(
                it,
                API(
                    true,
                    null,
                    "GG"
                )
            )
        }
    }
    install(Routing) {
        get("/") {
            call.respondText("HELLO WORLD!")
        }
        route("/api/") {
            login()
            video()
            classSystem()
            student()
        }
    }
}

val ApplicationCall.user get() = authentication.principal<User>()