package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RequestNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.ICheckHasNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IFetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IInitializeAllNotificationChannelsSyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IRequestNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IRescheduleAllScheduledNotificationAsyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalNotificationUseCaseSingletonModule {
    @Provides
    @Singleton
    fun provideRescheduleAllScheduledNotificationAsyncUseCase(
        alarmScheduler: AlarmScheduler,
        dao: LocalNotificationDao,
    ): RescheduleAllScheduledNotificationAsyncUseCase {
        return IRescheduleAllScheduledNotificationAsyncUseCase(
            alarmScheduler = alarmScheduler,
            // 通常のユースケース用でSingletonではないのでここで構築する
            fetchAllLocalNotificationUseCase = IFetchAllLocalNotificationAsyncUseCase(dao),
        )
    }

    @Provides
    @Singleton
    fun provideInitializeAllNotificationChannelsSyncUseCase(
        localNotificationScheduler: LocalNotificationScheduler,
    ): InitializeAllNotificationChannelsSyncUseCase {
        return IInitializeAllNotificationChannelsSyncUseCase(localNotificationScheduler)
    }

    @Provides
    @Singleton
    fun provideCheckHasNotificationPermissionSyncUseCase(
        localNotificationScheduler: LocalNotificationScheduler,
    ): CheckHasNotificationPermissionSyncUseCase {
        return ICheckHasNotificationPermissionSyncUseCase(localNotificationScheduler)
    }

    @Provides
    @Singleton
    fun provideRequestNotificationPermissionSyncUseCase(
        localNotificationScheduler: LocalNotificationScheduler,
    ): RequestNotificationPermissionSyncUseCase {
        return IRequestNotificationPermissionSyncUseCase(localNotificationScheduler)
    }
}
