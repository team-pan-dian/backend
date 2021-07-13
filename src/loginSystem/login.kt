package com.hack.loginSystem

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*

fun Route.login() {
    
    data class User(
        val Name: String,
        val Password: String
    )

    post("/login") {
        val userlogin = call.receive<User>()
        var userId: String? = null
    }
}