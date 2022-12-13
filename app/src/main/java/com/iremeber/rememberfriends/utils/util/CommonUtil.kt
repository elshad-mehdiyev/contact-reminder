package com.iremeber.rememberfriends.utils.util

import kotlin.random.Random

object CommonUtil {
    fun getColors(): String {
        val list = listOf("#1155FD", "#FF120C", "#35026A", "#007D2C", "#FEA50F", "#FCCE18",
            "#FF7DFD", "#00CEC4", "#8251FC")
        val i = Random.nextInt(0,9)
        return list[i]
    }
}