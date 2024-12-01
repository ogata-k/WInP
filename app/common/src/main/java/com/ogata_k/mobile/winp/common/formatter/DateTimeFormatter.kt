package com.ogata_k.mobile.winp.common.formatter

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun buildFullDateTimePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
}

fun buildFullDatePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd")
}

fun buildFullYearMonthPatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("yyyy/MM")
}

fun buildFullTimePatternFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern("HH:mm")
}

/**
 * 年月日時間のフォーマットに変換する。nullが値として渡されているならダミーを出力
 */
fun formatFullDateTimeOrEmpty(dateTime: LocalDateTime?): String {
    return if (dateTime == null) {
        "----/--/-- --:--"
    } else {
        buildFullDateTimePatternFormatter().format(dateTime)
    }
}

/**
 * 年月日のフォーマットに変換する。nullが値として渡されているならダミーを出力
 */
fun formatFullDateOrEmpty(dateTime: LocalDate?): String {
    return if (dateTime == null) {
        "----/--/--"
    } else {
        buildFullDatePatternFormatter().format(dateTime)
    }
}

/**
 * 年月のフォーマットに変換する。nullが値として渡されているならダミーを出力
 */
fun formatFullYearMonthOrEmpty(dateTime: LocalDate?): String {
    return if (dateTime == null) {
        "----/--"
    } else {
        buildFullYearMonthPatternFormatter().format(dateTime)
    }
}

/**
 * 時間のフォーマットに変換する。nullが値として渡されているならダミーを出力
 */
fun formatFullTimeOrEmpty(dateTime: LocalTime?): String {
    return if (dateTime == null) {
        "--:--"
    } else {
        buildFullTimePatternFormatter().format(dateTime)
    }
}