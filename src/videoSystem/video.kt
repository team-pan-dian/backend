package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.Class
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.video() {

    data class Video(
        val ID: String,
        val name: String,
        val count: Int,
        val view_count: Int,
        val url: String,
        val information: String,
    )
    
    data class    
   
    get("/class/{classnum}") {
        val classID = call.request.queryParameters["classnum"]?.toInt()
        if (classID == null) call.respond(mapOf("Fail" to true))
        else {
            var response: 
        }
    }

    get("/video/{number}") {
        val videoID = call.request.queryParameters["number"]?.toInt()
        if (videoID == null) call.respond(mapOf("Fail" to true))
        else {
            var response: Video? = null
            transaction {
                val request = Class.select {
                    Class.id.eq(videoID)
                }.first()
                val video = searchVideo(request[Class.videoList])
                response = Video(
                    ID = request[Class.id].toString(),
                    name = request[Class.name],
                    count = request[Class.count],
                    view_count = video[viewCount],
                    url = request[Class.url],
                    information = request[Class],
                )
            }
            call.respond(
                API(
                    data = response,
                    error = response == null,
                    errorMessage = if (response == null) "" else ""
                )
            )
        }
    }
    post("/video") {

    }
}
