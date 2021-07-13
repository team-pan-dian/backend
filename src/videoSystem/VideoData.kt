package com.hack.videoSystem

data class VideoData(
    val id: Int,
    val name: String,
    val url: String,
    val collect: Boolean,
    val information: String,
    val viewCount: Int,
    val order: Int
)