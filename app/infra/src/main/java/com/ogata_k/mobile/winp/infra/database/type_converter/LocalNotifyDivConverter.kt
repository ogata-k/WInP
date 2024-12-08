package com.ogata_k.mobile.winp.infra.database.type_converter

import androidx.room.TypeConverter
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv


object LocalNotifyDivConverter {
    @TypeConverter
    @JvmStatic
    fun toLocalNotifyDiv(value: Int): LocalNotifyDiv {
        return LocalNotifyDiv.lookup(value)
    }

    @TypeConverter
    @JvmStatic
    fun toInt(enum: LocalNotifyDiv): Int {
        return enum.value
    }
}