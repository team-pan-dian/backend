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
    }
}