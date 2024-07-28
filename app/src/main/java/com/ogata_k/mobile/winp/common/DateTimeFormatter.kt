package com.ogata_k.mobile.winp.common

import java.time.format.DateTimeFormatter

fun buildFullDateTimePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
}

fun buildFullDatePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd")
}