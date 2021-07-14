package com.hack.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object StudentTable : Table() {
    val id = integer("StudentId").autoIncrement().primaryKey()
    val name = text("Name")
    val hashcode = text("Hashcode")
    val collect = integer("Collect")
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
    val id =  integer("VideoId").autoIncrement().primaryKey()
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
        StudentTable.insert {
            it[name] = "22"
            it[hashcode] = "aa"
            it[collect] = 10
            it[collects] = ""
            it[teacher] = false
        }

    }
}