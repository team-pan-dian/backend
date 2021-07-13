package com.hack.videoSystem

import com.google.gson.Gson
import com.hack.db.Video
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun searchVideo(videoList: String): List<VideoData> {
    val gson = Gson()
    val videoIdList = gson.fromJson(videoList, Array<Int>::class.java).toList()

    return transaction {
        Video.select {
            Video.id inList videoIdList
        }
    }.map { 
        VideoData(
            it[Video.id],
            it[Video.name],
            it[Video.url],
            it[Video.collect],
            it[Video.information],
            it[Video.viewCount],
            it[Video.order]
        )
    }
}