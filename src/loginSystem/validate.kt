package com.hack.loginSystem

import com.hack.db.Student
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun validate(userId: Int): Boolean {
    var isUser: Boolean? = null
    transaction {
        val userData = Student.select {
            Student.id.eq(userId)
        }.singleOrNull()
        isUser = userData != null
    }
    return isUser ?: false
}