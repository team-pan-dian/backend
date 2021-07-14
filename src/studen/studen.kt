package com.hack.studen

import com.google.gson.Gson
import com.hack.api.API
import com.hack.db.StudentTable
import com.hack.user
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


fun Route.student() {
    val gson = Gson()

    post("/student/start") {
        val videoId = call.request.queryParameters["video"] ?: throw MissingRequestParameterException("video")
        val userId = call.user?.id ?: throw BadRequestException("")

        transaction {
            StudentTable.update({ StudentTable.id.eq(userId) }) {
                val data = gson.fromJson(StudentTable.select {
                    id.eq(userId)
                }.first()[collects], Array<String>::class.java).toMutableList()
                if (data.contains(videoId)) throw BadRequestException("GG")
                data.add(videoId)
                println(data)
                it[collects] = gson.toJson(data)
            }
        }
        call.respond(
            API(
                false,
                "ok"
            )
        )
    }

    delete("/student/start") {
        val videoId = call.request.queryParameters["video"] ?: throw MissingRequestParameterException("video")
        val userId = call.user?.id ?: throw BadRequestException("")
        transaction {
            StudentTable.update({ StudentTable.id.eq(userId) }) {
                val data = gson.fromJson(StudentTable.select {
                    id.eq(userId)
                }.first()[collects], Array<String>::class.java).toMutableList()
                if (!data.contains(videoId)) throw BadRequestException("GG")
                data.remove(videoId)
                it[collects] = gson.toJson(data)
            }
        }

        call.respond(
            API(
                false,
                "ok"
            )
        )
    }
}