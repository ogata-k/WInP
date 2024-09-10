package com.ogata_k.mobile.winp.common.formatter

import java.time.format.DateTimeFormatter

fun buildFullDateTimePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
}

fun buildFullDatePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd")
}

fun buildFullTimePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("HH:mm")
}
