package com.hack.api

data class API (
    val error: Boolean = false,
    val data: Any?,
    val errorMessage: String? = ""
)