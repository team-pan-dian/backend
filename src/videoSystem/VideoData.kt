package com.hack.videoSystem

data class VideoData(
    val id: Int,
    val name: String,
    val token: String,
    val collect: Boolean,
    val information: String,
    val viewCount: Int,
    val order: Int
)