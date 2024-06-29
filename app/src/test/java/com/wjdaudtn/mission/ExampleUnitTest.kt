package com.wjdaudtn.mission

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun main() {
        val currentTimeMillis = System.currentTimeMillis()
        val formattedDate = formatDateTime(currentTimeMillis)
        println("Formatted Date: $formattedDate")
    }

    fun formatDateTime(milliseconds: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        calendar.timeInMillis = milliseconds

        val sdf = SimpleDateFormat("a h:mm M월 d일(EEE)", Locale.KOREA)
        sdf.timeZone = calendar.timeZone
        return sdf.format(calendar.time)
    }


}