package com.ogata_k.mobile.winp.common.type_converter

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneId

object LocalTimeConverter {
    @JvmStatic
    fun toOffsetTime(value: LocalTime): OffsetTime {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toOffsetDateTime()
        return value.atOffset(now.offset)
    }

    @JvmStatic
    fun fromOffsetTime(value: OffsetTime): LocalTime {
        return value.toLocalTime()
    }
}