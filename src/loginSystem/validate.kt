package com.hack.loginSystem

import com.hack.db.StudentTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun validate(userId: Int): Boolean {
    var isUser: Boolean? = null
    transaction {
        val userData = StudentTable.select {
            StudentTable.id.eq(userId)
        }.singleOrNull()
        isUser = userData != null
    }
    return isUser ?: false
}