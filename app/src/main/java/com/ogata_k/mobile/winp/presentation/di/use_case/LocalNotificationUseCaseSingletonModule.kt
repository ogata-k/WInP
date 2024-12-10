package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IFetchAllLocalNotificationAsyncUseCase
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
    fun provideFetchAllLocalNotificationAsyncUseCase(
        dao: LocalNotificationDao,
    ): FetchAllLocalNotificationAsyncUseCase {
        return IFetchAllLocalNotificationAsyncUseCase(dao)
    }
}
