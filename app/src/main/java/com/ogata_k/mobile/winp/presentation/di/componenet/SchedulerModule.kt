package com.ogata_k.mobile.winp.presentation.di.componenet

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.component.NotificationScheduler
import com.ogata_k.mobile.winp.presentation.component.IAlarmScheduler
import com.ogata_k.mobile.winp.presentation.component.INotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SchedulerModule {
    @Singleton
    @Provides
    fun providesAlarmScheduler(
        @ApplicationContext context: Context,
    ): AlarmScheduler = IAlarmScheduler(
        context = context,
        manager = context.getSystemService(AlarmManager::class.java)
    )

    @Singleton
    @Provides
    fun providesNotifyScheduler(
        @ApplicationContext context: Context,
    ): NotificationScheduler = INotificationScheduler(
        context = context,
        manager = context.getSystemService(NotificationManager::class.java)
    )
}