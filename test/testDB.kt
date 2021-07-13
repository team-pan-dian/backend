package com.hack

import com.hack.db.initDB
import com.hack.loginSystem.validate
import com.hack.videoSystem.searchVideo
import junit.framework.Assert.assertEquals
import org.junit.Test

class TestDB {

    @Test
    fun testDB() {
        initDB()
        assertEquals(validate(1), true)
    }

    @Test
    fun testVideoList() {
        initDB()
        assertEquals(searchVideo(1), false)
    }
}
