package com.hack.loginSystem

import at.favre.lib.crypto.bcrypt.BCrypt
import com.hack.api.API
import com.hack.db.StudentTable
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

data class UserData(
    val token: String,
    val teacher: Boolean,
    val collects: String,
    val name: String
)

fun Route.login() {

    post("/login") {
        val userLogin = call.receiveParameters()
        val userName = userLogin["name"]
        val userPassword = userLogin["password"]

        if (userName != null && userPassword != null) {
            var loginData: ResultRow? = null

            transaction {
                loginData = StudentTable.select {
                    StudentTable.name.eq(userName)
                }.firstOrNull()

                if (loginData != null)
                    loginData = if (
                        BCrypt.verifyer()
                            .verify(userPassword.toCharArray(), loginData?.get(StudentTable.hashcode)).verified
                    ) loginData
                    else null
            }

            if (loginData != null) {

                call.respond(
                    API(
                        false,
                        UserData(
                            JWTConfig.makeToken(
                                User(
                                    loginData!![StudentTable.id],
                                    loginData!![StudentTable.teacher]
                                )
                            ),
                            loginData!![StudentTable.teacher],
                            loginData!![StudentTable.collects],
                            loginData!![StudentTable.name]
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
                isUser = StudentTable.select {
                    StudentTable.name.eq(userName)
                }.firstOrNull()

                if (isUser == null)
                    StudentTable.insert {
                        it[name] = userName
                        it[hashcode] = BCrypt.withDefaults().hashToString(12, userPassword.toCharArray())
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
