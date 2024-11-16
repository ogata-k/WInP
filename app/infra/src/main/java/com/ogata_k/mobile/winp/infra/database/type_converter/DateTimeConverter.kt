package com.ogata_k.mobile.winp.infra.database.type_converter

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Room で日時を扱うためのコンバーター。
 *
 * Room が使用する SQLite には日時型がない（[Datatypes In SQLite](https://www.sqlite.org/datatype3.html)）ためその対策
 */
object DateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String): OffsetDateTime {
        return formatter.parse(value, OffsetDateTime::from)
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(value: OffsetDateTime): String {
        return value.format(formatter)
    }
}
