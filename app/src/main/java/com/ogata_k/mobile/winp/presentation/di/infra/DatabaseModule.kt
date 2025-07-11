package com.ogata_k.mobile.winp.presentation.di.infra

import android.content.Context
import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.impl.ILocalNotificationDao
import com.ogata_k.mobile.winp.infra.database.dao.impl.ISummaryWorkDao
import com.ogata_k.mobile.winp.infra.database.dao.impl.IWorkCommentDao
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
    ): AppDatabase = AppDatabase.createDatabase(context, "winp.db")

    @Singleton
    @Provides
    fun providesSummaryWorkDao(
        appDb: AppDatabase,
    ): SummaryWorkDao = ISummaryWorkDao(appDb, appDb.summaryWorkDao())

    @Singleton
    @Provides
    fun providesWorkWithWorkTodoDao(
        appDb: AppDatabase,
    ): WorkDao = IWorkDao(appDb, appDb.workWithWorkTodoDao())

    @Singleton
    @Provides
    fun providesWorkCommentDao(
        appDb: AppDatabase,
    ): WorkCommentDao = IWorkCommentDao(appDb, appDb.workCommentDao())

    @Singleton
    @Provides
    fun providesLocalNotificationDao(
        appDb: AppDatabase,
    ): LocalNotificationDao = ILocalNotificationDao(appDb, appDb.localNotificationDao())
}