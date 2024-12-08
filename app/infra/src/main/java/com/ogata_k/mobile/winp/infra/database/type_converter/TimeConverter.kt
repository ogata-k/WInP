package com.ogata_k.mobile.winp.infra.database.type_converter

import androidx.room.TypeConverter
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

/**
 * Room で時間を扱うためのコンバーター。
 *
 * Room が使用する SQLite には時間型がない（[Datatypes In SQLite](https://www.sqlite.org/datatype3.html)）ためその対策
 */
object TimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetTime(value: String): OffsetTime {
        return formatter.parse(value, OffsetTime::from)
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetTime(value: OffsetTime): String {
        return value.format(formatter)
    }
}