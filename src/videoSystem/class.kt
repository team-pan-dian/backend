package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.Class
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class ClassCopy(
    val id: String,
    val name: String,
    val count: Int,
    val information: String,
    val videoList: List<VideoData>,
    val img: String
)

fun Route.classSystem() {

    get("/class/{id}") {
        val classID = call.parameters["id"]?.toInt()
        if (classID == null) throw MissingRequestParameterException("Class Id")
        else {
            var response: ClassCopy? = null
            transaction {
                val request = Class.select {
                    Class.id.eq(classID)
                }.firstOrNull()
                response = if (request != null) {

                    val videoList = searchVideo(classID)
                    ClassCopy(
                        id = request[Class.id].toString(),
                        name = request[Class.name],
                        count = videoList.size,
                        information = request[Class.information],
                        videoList = videoList,
                        img =  request[Class.img]
                    )
                } else null
            }
            call.respond(
                API(
                    response == null,
                    response,
                    if (response == null) "Fail" else ""
                )
            )
        }
    }

    post("/class") {
        val receive = call.receiveParameters()
        val className = receive["name"]
        val classInformation = receive["info"]

        if (classInformation != null && className != null) {
            transaction {
                Class.insert {
                    it[name] = className
                    it[information] = classInformation
                    it[img] = ""
                }
            }
            call.respond(
                API(
                    false,
                    "Ok"
                )
            )
        } else throw MissingRequestParameterException("name or info")
    }

    delete("/class/{id}") {
        val classID = call.parameters["id"]?.toInt() ?: throw BadRequestException("The type of Id is wrong.")
        var classData: ResultRow? = null
        transaction {
            classData = Class.select {
                Class.id.eq(classID)
            }.firstOrNull()
            Class.deleteWhere { Class.id.eq(classID) }
        }

        if (classData == null) throw BadRequestException("")
        else call.respond(
            API(
                false,
                "Ok"
            )
        )
    }

    put("/class/{id}") {
        val classId = call.parameters["id"]?.toInt()
        val query = call.request.queryParameters
        val className = query["name"]
        val info = query["info"]

        if (classId != null && (className != null || info != null)) {
            var isClass = false
            transaction {
                isClass = Class.select {
                    Class.id.eq(classId)
                }.firstOrNull() != null
                Class.update({ Class.id.eq(classId) }) {
                    if (className != null)
                        it[name] = className
                    if (info != null) {
                        it[information] = info
                    }
                }
            }

            if (isClass) {
                call.respond(
                    API(
                        false,
                        "Ok"
                    )
                )
            } else throw BadRequestException("")

        } else throw MissingRequestParameterException("name or info")
    }
}
