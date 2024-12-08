package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IDeleteLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IFetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IGetLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.local_notification.IUpsertLocalNotificationAsyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object LocalNotificationUseCaseModule {
    @Provides
    fun provideFetchAllLocalNotificationAsyncUseCase(
        dao: LocalNotificationDao,
    ): FetchAllLocalNotificationAsyncUseCase {
        return IFetchAllLocalNotificationAsyncUseCase(dao)
    }

    @Provides
    fun provideGetLocalNotificationAsyncUseCase(
        dao: LocalNotificationDao,
    ): GetLocalNotificationAsyncUseCase {
        return IGetLocalNotificationAsyncUseCase(dao)
    }

    @Provides
    fun provideUpsertLocalNotificationAsyncUseCase(
        dao: LocalNotificationDao,
    ): UpsertLocalNotificationAsyncUseCase {
        return IUpsertLocalNotificationAsyncUseCase(dao)
    }

    @Provides
    fun provideDeleteLocalNotificationAsyncUseCase(
        dao: LocalNotificationDao,
    ): DeleteLocalNotificationAsyncUseCase {
        return IDeleteLocalNotificationAsyncUseCase(dao)
    }
}