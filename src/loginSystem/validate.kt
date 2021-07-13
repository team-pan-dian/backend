package com.hack.loginSystem

import com.hack.db.Student
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun validate(userId: Int): Boolean {
    transaction {
        val userData = Student.select {
            Student.id.eq(userId)
        }.first()
        val isUser = userData[Student.id]
    }
    return true
}