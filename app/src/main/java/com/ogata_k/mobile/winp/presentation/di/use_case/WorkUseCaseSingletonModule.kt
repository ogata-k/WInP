package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IGetSummaryWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.INotifyForWorkAsyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkUseCaseSingletonModule {
    @Provides
    @Singleton
    fun provideNotifyWorkAsyncUseCase(
        localNotificationScheduler: LocalNotificationScheduler,
        summaryWorkDao: SummaryWorkDao,
    ): NotifyForWorkAsyncUseCase {
        // GetSummaryWorkAsyncUseCaseがSingletonModule以外で提供されているのでこの中で構築する
        return INotifyForWorkAsyncUseCase(
            localNotificationScheduler,
            IGetSummaryWorkAsyncUseCase(summaryWorkDao)
        )
    }
}