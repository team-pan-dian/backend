package com.hack

import com.hack.db.initDB
import com.hack.loginSystem.validate
import com.hack.videoSystem.searchVideo
import org.junit.Test

class TestDB {

    @Test
    fun testDB() {
        initDB()
        validate(1)
    }

    @Test
    fun testVideoList() {
        initDB()
        println(searchVideo("[1,2]"))
    }
}
