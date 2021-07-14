package com.hack.videoSystem

data class VideoData(
    val id: Int,
    val name: String,
    val information: String,
    val viewCount: Int,
    val sequence: Int,
    val classId: Int,
    val fileName: String
)