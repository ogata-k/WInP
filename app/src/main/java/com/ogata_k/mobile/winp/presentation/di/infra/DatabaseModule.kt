package com.ogata_k.mobile.winp.presentation.di.infra

import android.content.Context
import androidx.room.Room
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.impl.IWorkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun providesRoomDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room
        .databaseBuilder(context, AppDatabase::class.java, "winp.db")
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun providesWorkWithWorkTodoDao(
        appDb: AppDatabase,
    ): WorkDao = IWorkDao(appDb, appDb.workWithWorkTodoDao())
}