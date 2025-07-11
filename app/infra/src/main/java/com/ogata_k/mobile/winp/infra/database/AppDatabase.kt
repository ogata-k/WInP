package com.ogata_k.mobile.winp.infra.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ogata_k.mobile.winp.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.infra.database.dao.WorkWithWorkTodoDao
import com.ogata_k.mobile.winp.infra.database.entity.LocalNotification
import com.ogata_k.mobile.winp.infra.database.entity.Work
import com.ogata_k.mobile.winp.infra.database.entity.WorkComment
import com.ogata_k.mobile.winp.infra.database.entity.WorkTodo
import com.ogata_k.mobile.winp.infra.database.type_converter.DateTimeConverter
import com.ogata_k.mobile.winp.infra.database.type_converter.LocalNotifyDivConverter
import com.ogata_k.mobile.winp.infra.database.type_converter.TimeConverter

@Database(
    exportSchema = true,
    entities = [Work::class, WorkTodo::class, WorkComment::class, LocalNotification::class],
    version = 3,
    autoMigrations = [
        // add work_comments table for WorkComment::class
        AutoMigration(
            from = 1,
            to = 2,
        ),
        // add local_notifications table for LocalNotification::class
        AutoMigration(
            from = 2,
            to = 3,
        ),
    ],
)
@TypeConverters(value = [DateTimeConverter::class, TimeConverter::class, LocalNotifyDivConverter::class])
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun createDatabase(context: Context, dbName: String): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, dbName)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun summaryWorkDao(): SummaryWorkDao
    abstract fun workWithWorkTodoDao(): WorkWithWorkTodoDao
    abstract fun workCommentDao(): WorkCommentDao
    abstract fun localNotificationDao(): LocalNotificationDao
}