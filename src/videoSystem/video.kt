package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.Video
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File



fun Route.video() {

    data class VideoUpload(
        val ID: String,
        val information: String,
    )

    put("/class/{classnum}") {
        val requestId = call.parameters["classnum"]?.toInt() ?: throw BadRequestException("The type of Id is wrong.")

        transaction {

        }
    }




    get("/video/{number}") {
        val videoID = call.request.queryParameters["number"]?.toInt()
        if (videoID == null) call.respond(mapOf("Fail" to true))
        else {
            var response: VideoData? = null
            transaction {
                val video = Video.select {
                    Video.id.eq(videoID)
                }.first()

                response = VideoData(
                    id = video[Video.id],
                    name = video[Video.name],
                    token = video[Video.url],
                    collect = video[Video.collect],
                    viewCount = video[Video.viewCount],
                    information = video[Video.information],
                    sequence = video[Video.sequence],
                    classId = video[Video.classId]
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
        val videoData = call.receiveMultipart()
        var fileName: String

        videoData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    val fileNameExtension = fileName.split(".")
                    if (fileNameExtension[fileNameExtension.size - 1] != "mp4") {
                        call.respond(mapOf("Fail" to true))
                    } else {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("video/$fileName").writeBytes(fileBytes)
                    }
                    call.respond(mapOf("OK" to true))
                }
            }
        }
    }
}