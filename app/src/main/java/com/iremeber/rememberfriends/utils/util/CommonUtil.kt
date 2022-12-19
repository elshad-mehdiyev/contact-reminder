package com.iremeber.rememberfriends.utils.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object CommonUtil {
    fun getColors(): String {
        val list = listOf("#1155FD", "#FF120C", "#35026A", "#007D2C", "#FEA50F", "#FCCE18",
            "#FF7DFD", "#00CEC4", "#8251FC")
        val i = Random.nextInt(0,9)
        return list[i]
    }
    fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}