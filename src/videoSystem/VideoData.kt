package com.hack.videoSystem

data class VideoData(
    val id: String,
    val name: String,
    val information: String,
    val viewCount: Int,
    val sequence: Int,
    val classId: String,
    val fileName: String
)