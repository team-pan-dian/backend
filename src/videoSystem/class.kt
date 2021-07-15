package com.hack.videoSystem

import com.hack.api.API
import com.hack.db.ClassesTable
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class ClassCopy(
    val id: String,
    val name: String,
    val count: Int? = null,
    val information: String,
    val videoList: List<VideoData>? = null,
    val img: String,
    val type: String
)

fun Route.classSystem() {
    // ok
    get("/classes") {
        var data: List<ClassCopy>? = null
        transaction {
            data = ClassesTable.selectAll().map {
                ClassCopy(
                    id = it[ClassesTable.id].toString(),
                    name = it[ClassesTable.name],
                    information = it[ClassesTable.information],
                    img =  it[ClassesTable.img],
                    type = it[ClassesTable.type]
                )
            }
        }

        if (data != null) call.respond(
            API(
                false,
                data
            )
        ) else throw BadRequestException("Fail")
    }
    // ok
    get("/class/{id}") {
        val classID = call.parameters["id"]?.toInt()
        if (classID == null) throw MissingRequestParameterException("Class Id")
        else {
            var response: ClassCopy? = null
            transaction {
                val request = ClassesTable.select {
                    ClassesTable.id.eq(classID)
                }.firstOrNull()
                response = if (request != null) {

                    val videoList = searchVideo(classID)
                    ClassCopy(
                        id = request[ClassesTable.id].toString(),
                        name = request[ClassesTable.name],
                        count = videoList.size,
                        information = request[ClassesTable.information],
                        videoList = videoList,
                        img =  request[ClassesTable.img],
                        type = request[ClassesTable.type]
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
    // ok
    authenticate {
        post("/class") {
            val receive = call.receiveParameters()
            val className = receive["name"]
            val classInformation = receive["info"]
            val classType = receive["type"]

            if (classInformation != null && className != null && classType != null) {
                transaction {
                    ClassesTable.insert {
                        it[name] = className
                        it[information] = classInformation
                        it[img] = ""
                        it[type] = classType
                    }
                }
                call.respond(
                    API(
                        false,
                        "Ok"
                    )
                )
            } else throw MissingRequestParameterException("name or info or type")
        }

        delete("/class/{id}") {
            val classID = call.parameters["id"]?.toInt() ?: throw BadRequestException("The type of Id is wrong.")
            var classData: ResultRow? = null
            transaction {
                classData = ClassesTable.select {
                    ClassesTable.id.eq(classID)
                }.firstOrNull()
                ClassesTable.deleteWhere { ClassesTable.id.eq(classID) }
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
                    isClass = ClassesTable.select {
                        ClassesTable.id.eq(classId)
                    }.firstOrNull() != null
                    ClassesTable.update({ ClassesTable.id.eq(classId) }) {
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

    put("class/{ClassID}/{VideoID}/order") {

    }

}
