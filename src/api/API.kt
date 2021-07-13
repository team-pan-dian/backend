package com.hack.api

data class API <T> (
    val error: Boolean = false,
    val data: T?,
    val errorMessage: String? = ""
)