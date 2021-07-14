package com.hack.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Student : Table() {
    val id = integer("StudentId").autoIncrement().primaryKey()
    val name = text("Name")
    val hashcode = text("Hashcode")
    val collect = integer("Collect")
    val collects = text("Collects") // json
    val teacher = bool("teacher")
}

object Class : Table() {
    val id = integer("ClassId").autoIncrement().primaryKey()
    val name = text("Name")
    val information = text("Information")
    val img = text("img")
}

object Video : Table() {
    val id =  integer("VideoId").autoIncrement().primaryKey()
    val name = text("Name")
    val url = text("URL")
    val classId = integer("ClassId")
    val collect = bool("Collect")
    val information = text("Information")
    val viewCount = integer("ViewCount")
    val sequence = integer("Sequence")
}

fun initDB() {
    val config = HikariConfig("/hikari.properties")
    config.schema = "public"
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(Student, Class, Video)
        Student.insert {
            it[name] = "22"
            it[hashcode] = "aa"
            it[collect] = 10
            it[collects] = ""
            it[teacher] = false
        }
    }
}