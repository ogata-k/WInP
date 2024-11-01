package com.ogata_k.mobile.winp.common.type_converter

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId


object LocalDateTimeConverter {
    @JvmStatic
    fun toOffsetDateTime(value: LocalDateTime): OffsetDateTime {
        val zoneId = ZoneId.systemDefault()
        return value.atZone(zoneId).toOffsetDateTime()
    }

    @JvmStatic
    fun fromOffsetDateTime(value: OffsetDateTime): LocalDateTime {
        return value.toLocalDateTime()
    }
}