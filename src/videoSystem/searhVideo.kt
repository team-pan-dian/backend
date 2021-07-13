package com.hack.videoSystem

import com.google.gson.Gson
import com.hack.db.Class
import com.hack.db.Video
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun searchVideo(classId: Int): List<VideoData> {
    return transaction {
        Video.select {
            Video.classId.eq(classId)
        }.sortedBy {
            it[Video.sequence]
        }
    }.map {
        VideoData(
            it[Video.id],
            it[Video.name],
            it[Video.token],
            it[Video.collect],
            it[Video.information],
            it[Video.viewCount],
            it[Video.sequence],
            it[Video.classId]
        )
    }
}