package com.example.demosetlanguage

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.demosetlanguage.data.GenericUtils
import com.example.demosetlanguage.data.TestN
import com.example.demosetlanguage.data.TestNBaseResp
import com.example.demosetlanguage.data.User1
import com.example.demosetlanguage.data.User2

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.demosetlanguage", appContext.packageName)
    }

    @Test
    fun testTestNBaseRespPrintName() {
        // val ab = TestNBaseResp<TestN<User1, User2>>()
        val ab = object : TestNBaseResp<TestN<User1, User2>>() {}
        println("hello world1: ${ab.printName()}")
        println("hello world2: ${ab.getDataClass()}")
        println("hello world3: ${ab.getType()}")
    }
}