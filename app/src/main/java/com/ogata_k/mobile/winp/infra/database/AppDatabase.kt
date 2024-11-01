package com.ogata_k.mobile.winp.infra.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ogata_k.mobile.winp.infra.database.dao.WorkWithWorkTodoDao
import com.ogata_k.mobile.winp.infra.database.entity.Work
import com.ogata_k.mobile.winp.infra.database.entity.WorkTodo
import com.ogata_k.mobile.winp.infra.database.type_converter.DateTimeConverter

@Database(
    entities = [Work::class, WorkTodo::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workWithWorkTodoDao(): WorkWithWorkTodoDao
}