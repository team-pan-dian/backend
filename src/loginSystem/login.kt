package com.hack.loginSystem

import com.hack.api.API
import com.hack.db.Student
import io.ktor.application.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.login() {

    post("/login") {
        val userLogin = call.receiveParameters()
        val userName = userLogin["Name"]
        val userPassword = userLogin["Password"]

        if (userName != null && userPassword != null) {
            var loginData: ResultRow? = null
            println("$userName $userPassword")
            transaction {
                loginData = Student.select {
                    Student.name.eq(userName).and(
                        Student.password.eq(userPassword)
                    )
                }.firstOrNull()
            }

            if (loginData != null) {
                call.sessions.set(
                    TokenSession(
                        JWTConfig.makeToken(
                            User(loginData!![Student.id], loginData!![Student.name])
                        )
                    )
                )
                call.respond(
                    API(
                        false,
                        "ok",
                    )
                )
            } else call.respond(
                HttpStatusCode.Forbidden, API(
                    true,
                    null,
                    "Fail"
                )
            )

        } else call.respond(
            HttpStatusCode.UnprocessableEntity,
            API(
               true,
                null,
                "missing parameter"
            )
        )

    }
    data class User(
        val name: String,
        val password: String,
    )

    post("/create") {
        val UserInformation = calreceive<User>()
        try {
            transaction {

            }
        }
    }
}