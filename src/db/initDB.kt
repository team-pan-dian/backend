package com.hack.db

import at.favre.lib.crypto.bcrypt.BCrypt
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StudentTable : Table() {
    val id = integer("StudentId").autoIncrement().primaryKey()
    val name = text("Name")
    val hashcode = text("Hashcode")
    val collects = text("Collects") // json
    val teacher = bool("teacher")
}

object ClassesTable : Table() {
    val id = integer("ClassId").autoIncrement().primaryKey()
    val name = text("Name")
    val information = text("Information")
    val type = text("Type")
    val img = text("img")
}

object VideoTable : Table() {
    val id = integer("VideoId").autoIncrement().primaryKey()
    val name = text("Name")
    val classId = integer("ClassId")
    val fileName = text("FileName")
    val information = text("Information")
    val viewCount = integer("ViewCount")
    val sequence = integer("Sequence").autoIncrement()
}

fun initDB() {
    val config = HikariConfig("/hikari.properties")
    config.schema = "public"
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(StudentTable, ClassesTable, VideoTable)
        StudentTable.select {
            StudentTable.name.eq("teacher")
        }.firstOrNull() ?: StudentTable.insert {
            it[name] = "teacher"
            it[hashcode] = BCrypt.withDefaults().hashToString(12, "password".toCharArray())
            it[collects] = "[]"
            it[teacher] = true
        }
        val id = ClassesTable.insert {
            it[name] = "沒有駭客學校，但你可以自學。"
            it[information] = "這堂課程可以讓您從零基礎，搖身一遍成為資安高手。"
            it[type] = "資訊, 資安, 駭客"
            it[img] = "https://images.unsplash.com/photo-1542831371-29b0f74f9713"
        }[ClassesTable.id]
        VideoTable.insert {
            it[name] = "何為駭客？"
            it[information] = "在成為駭客之前，你得先了解駭客文化是什麼。"
            it[fileName] = "https://download.samplelib.com/mp4/sample-15s.mp4"
            it[classId] = id
            it[viewCount] = 0
        }

        VideoTable.insert {
            it[name] = "設定環境"
            it[information] = "工先利其器。"
            it[fileName] = "https://download.samplelib.com/mp4/sample-15s.mp4"
            it[classId] = id
            it[viewCount] = 0
        }
    }
}