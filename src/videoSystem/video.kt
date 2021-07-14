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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.*


fun Route.video() {

    data class VideoUpload(
        val ID: String,
        val information: String,
    )


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

    put("/class/{classID}/{videoID}") {
        val videoId = call.parameters["videoID"]?.toInt()
        val query = call.request.queryParameters
        val videoName = query["name"]
        val info = query["info"]

        if (videoId != null && (videoName != null || info != null)) {
            var isVideo = false
            transaction {
                isVideo = Video.select {
                    Video.id.eq(videoId)
                }.firstOrNull() != null
                Video.update({ Video.id.eq(videoId) }) {
                    if (videoName != null)
                        it[name] = videoName
                    if (info != null)
                        it[information] = info
                }
            }
            if (isVideo) {
                call.respond(
                    API(
                        false,
                        "Ok"
                    )
                )
            } else throw BadRequestException("")
        } else throw MissingRequestParameterException("name or info")
    }

    post("/class/{classID}") {
        val videoData = call.receiveMultipart()
        val receive = call.request.queryParameters
        val videoInfo = receive["info"]
        val videoName = receive["name"]
        var fileName: String? = null
        val classId = call.parameters["classID"]?.toInt()
        var respondVideoData: VideoData? = null

        if (classId == null) throw BadRequestException("GG")
        if (videoInfo == null || videoName == null) throw MissingRequestParameterException("video Info or name")

        videoData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    fileName = Date().time.toString() + part.originalFileName as String
                    val fileNameExtension = fileName!!.split(".")
                    if (fileNameExtension[fileNameExtension.size - 1] != "mp4") {
                        println(fileNameExtension)
                        throw BadRequestException("Fail")
                    } else {
                        val fileBytes = part.streamProvider().readBytes()
                        File("H:\\動態桌布\\$fileName").writeBytes(fileBytes)
                    }
                }
            }
        }
        if (fileName == null) throw MissingRequestParameterException("file")
        else {

            transaction {
                val data = Video.insert {
                    it[name] = videoName
                    it[information] = videoInfo
                    it[Video.fileName] = fileName
                    it[viewCount] = 0
                    it[Video.classId] = classId
                }
                respondVideoData = VideoData(
                    data[Video.id],
                    videoName,
                    videoInfo,
                    0,
                    data[Video.sequence],
                    classId,
                    fileName!!
                )
            }
            if (respondVideoData != null)
                call.respond(
                    API(
                        false,
                        respondVideoData
                    )
                )
            else throw BadRequestException("Fail")
        }
    }
}