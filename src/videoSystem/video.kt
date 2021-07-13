package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.Class
import com.hack.db.Class.autoIncrement
import com.hack.db.Class.primaryKey
import com.hack.db.Video
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File

fun Route.video() {

    data class ClassCopy(
        val id: String,
        val name: String,
        val count: Int,
        val information: String,
        val videoList: List<VideoData>
    )

    data class VideoUpload(
        val ID: String,
        val information: String,
    )

    get("/class/{classnum}") {
        val classID = call.request.queryParameters["classnum"]?.toInt()
        if (classID == null) call.respond(API(true, null, "Fail"))
        else {
            var response: ClassCopy? = null
            transaction {
                val request = Class.select {
                    Class.id.eq(classID)
                }.first()
                val videoList = searchVideo(request[Class.id])
                response = ClassCopy(
                    id = request[Class.id].toString(),
                    name = request[Class.name],
                    count = request[Class.count],
                    information = request[Class.information],
                    videoList = videoList
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

    put("/class/{classnum}") {
        val requestId = call.parameters["classnum"]?.toInt() ?: throw BadRequestException("The type of Id is wrong.")

        transaction {

        }
  }

    delete("class/{classnum}") {
        val requestId = call.parameters["classnum"]?.toInt() ?: throw BadRequestException("The type of Id is wrong.")
        transaction {
            Class.deleteWhere { Class.id.eq(requestId) }
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
                    token = video[Video.token],
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