package com.ogata_k.mobile.winp.common.formatter

import java.time.LocalDateTime
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

/**
 * 年月日時間のフォーマットに変換する。nullが値として渡されているならダミーを出力
 */
fun formatFullDateTimeOrEmpty(dateTime: LocalDateTime?): String {
    return if (dateTime == null) {
        "----/--/-- --:--"
    } else {
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(dateTime)
    }
}