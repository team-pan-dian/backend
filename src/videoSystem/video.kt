package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.VideoTable
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.File
import java.util.*


fun Route.video() {
    // ok
    get("/class/{classId}/{id}") {
        val videoID = call.parameters["id"]?.toInt()
        if (videoID == null) throw MissingRequestParameterException("video id")
        else {
            var response: VideoData? = null
            transaction {
                val video = VideoTable.select {
                    VideoTable.id.eq(videoID)
                }.firstOrNull()
                if (video != null) {
                    response = VideoData(
                        id = video[VideoTable.id],
                        name = video[VideoTable.name],
                        viewCount = video[VideoTable.viewCount],
                        information = video[VideoTable.information],
                        sequence = video[VideoTable.sequence],
                        classId = video[VideoTable.classId],
                        fileName = video[VideoTable.fileName]
                    )

                }
                else throw BadRequestException("Fail")
            }
            call.respond(
                API(
                    data = response,
                    error = response == null,
                    errorMessage = if (response == null) "Fail" else ""
                )
            )
        }
    }
    // ok

    authenticate {
        put("/class/{classID}/{videoID}") {
            val videoId = call.parameters["videoID"]?.toInt()
            val query = call.request.queryParameters
            val videoName = query["name"]
            val info = query["info"]

            if (videoId != null && (videoName != null || info != null)) {
                var isVideo = false
                transaction {
                    isVideo = VideoTable.select {
                        VideoTable.id.eq(videoId)
                    }.firstOrNull() != null
                    VideoTable.update({ VideoTable.id.eq(videoId) }) {
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
                } else throw BadRequestException("Fail")
            } else throw MissingRequestParameterException("name or info")
        }
        // ok
        post("/class/{classID}/upload") {
            val videoData = call.receiveMultipart()
            val receive = call.request.queryParameters
            val videoInfo = receive["info"]
            val videoName = receive["name"]
            var fileName: String? = null
            val classId = call.parameters["classID"]?.toInt()
            var respondVideoData: VideoData? = null

            if (classId == null) throw BadRequestException("Fail")
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
                    val data = VideoTable.insert {
                        it[name] = videoName
                        it[information] = videoInfo
                        it[VideoTable.fileName] = fileName
                        it[viewCount] = 0
                        it[VideoTable.classId] = classId
                    }
                    respondVideoData = VideoData(
                        data[VideoTable.id],
                        videoName,
                        videoInfo,
                        0,
                        data[VideoTable.sequence],
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

}