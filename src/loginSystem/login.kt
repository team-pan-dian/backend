package com.hack.loginSystem

import at.favre.lib.crypto.bcrypt.BCrypt
import com.hack.api.API
import com.hack.db.Student
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.login() {

    post("/login") {
        val userLogin = call.receiveParameters()
        val userName = userLogin["name"]
        val userPassword = userLogin["password"]

        if (userName != null && userPassword != null) {
            var loginData: ResultRow? = null

            transaction {
                loginData = Student.select {
                    Student.name.eq(userName)
                }.firstOrNull()

                if (loginData != null)
                    loginData = if (
                        BCrypt.verifyer()
                            .verify(userPassword.toCharArray(), loginData?.get(Student.hashcode)).verified
                    ) loginData
                    else null
            }

            if (loginData != null) {

                call.respond(
                    API(
                        false,
                        JWTConfig.makeToken(
                            User(loginData!![Student.id], loginData!![Student.name])
                        )
                    )
                )
            } else call.respond(
                HttpStatusCode.Forbidden, API(
                    true,
                    null,
                    "Fail"
                )
            )

        } else throw MissingRequestParameterException("name or password")

    }

    post("/sign-up") {
        val userInformation = call.receiveParameters()
        val userName = userInformation["name"]
        val userPassword = userInformation["password"]

        if (userName != null && userPassword != null) {
            var isUser: ResultRow? = null

            transaction {
                isUser = Student.select {
                    Student.name.eq(userName)
                }.firstOrNull()

                if (isUser == null)
                    Student.insert {
                        it[name] = userName
                        it[hashcode] = BCrypt.withDefaults().hashToString(12, userPassword.toCharArray())
                        it[collect] = 0
                        it[collects] = "[]"
                        it[teacher] = false
                    }
            }

            if (isUser == null) {
                call.respond(
                    API(
                        false,
                        "Ok"
                    )
                )
            } else throw BadRequestException("Used")

        } else throw MissingRequestParameterException("name or password")
    }
}
