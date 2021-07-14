package com.hack.videoSystem

import com.hack.db.VideoTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun searchVideo(classId: Int): List<VideoData> {
    return transaction {
        VideoTable.select {
            VideoTable.classId.eq(classId)
        }.sortedBy {
            it[VideoTable.sequence]
        }
    }.map {
        VideoData(
            it[VideoTable.id],
            it[VideoTable.name],
            it[VideoTable.information],
            it[VideoTable.viewCount],
            it[VideoTable.sequence],
            it[VideoTable.classId],
            it[VideoTable.fileName]
        )
    }
}