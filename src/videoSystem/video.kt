package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.Video
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*


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




    get("/class/{classId}/{id}") {
        val videoID = call.parameters["id"]?.toInt()
        if (videoID == null) throw MissingRequestParameterException("video id")
        else {
            var response: VideoData? = null
            transaction {
                val video = Video.select {
                    Video.id.eq(videoID)
                }.firstOrNull()

                if (video != null)
                    response = VideoData(
                        id = video[Video.id],
                        name = video[Video.name],
                        viewCount = video[Video.viewCount],
                        information = video[Video.information],
                        sequence = video[Video.sequence],
                        classId = video[Video.classId],
                        fileName = video[Video.fileName]
                    )
                else throw BadRequestException("")
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

    post("/class/{classID}") {
        val videoData = call.receiveMultipart()
        val receive = call.receiveParameters()
        val videoInfo = receive["info"]
        val videoName = receive["name"]
        val filesName = mutableListOf<String>()
        val classId = call.parameters["classID"]?.toInt()
        var respondVideoData: VideoData? = null

        if (videoInfo == null || videoName == null) throw MissingRequestParameterException("video Info")
        videoData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val fileName = Date().time.toString() + part.originalFileName as String
                    filesName.add(fileName)
                    val fileNameExtension = fileName.split(".")
                    if (fileNameExtension[fileNameExtension.size - 1] != "mp4") {
                        throw BadRequestException("Fail")
                    } else {
                        val fileBytes = part.streamProvider().readBytes()
                        File("video/$fileName").writeBytes(fileBytes)
                    }
                }
            }
        }
        if (filesName.size == 0) throw MissingRequestParameterException("file")
        else {
            filesName.map { videoFileName ->

                transaction {
                    val a = Video.insert {
                        it[name] = videoName
                        it[information] = videoInfo
                        it[fileName] = videoFileName
                        it[viewCount] = 0
                        it[Video.classId] = classId
                    }
                    a[Video.id]
                }

            }
        }
    }
}