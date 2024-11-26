package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.ICreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.ICreateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IDeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IFetchAllWorkCommentsAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IFetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IGetSummaryWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IGetWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IUpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IUpdateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IUpdateWorkTodoStateAsyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object WorkUseCaseModule {
    @Provides
    fun provideGetSummaryAsyncUseCase(
        summaryWorkDao: SummaryWorkDao,
    ): GetSummaryAsyncUseCase {
        return IGetSummaryWorkAsyncUseCase(summaryWorkDao)
    }

    @Provides
    fun provideFetchPageWorksAsyncUseCase(
        workDao: WorkDao,
    ): FetchPageWorksAsyncUseCase {
        return IFetchPageWorksAsyncUseCase(workDao)
    }

    @Provides
    fun provideGetWorkAsyncUseCase(
        workDao: WorkDao,
    ): GetWorkAsyncUseCase {
        return IGetWorkAsyncUseCase(workDao)
    }

    @Provides
    fun provideCreateWorkAsyncUseCase(
        workDao: WorkDao,
    ): CreateWorkAsyncUseCase {
        return ICreateWorkAsyncUseCase(workDao)
    }

    @Provides
    fun provideUpdateWorkAsyncUseCase(
        workDao: WorkDao,
    ): UpdateWorkAsyncUseCase {
        return IUpdateWorkAsyncUseCase(workDao)
    }

    @Provides
    fun provideDeleteWorkAsyncUseCase(
        workDao: WorkDao,
    ): DeleteWorkAsyncUseCase {
        return IDeleteWorkAsyncUseCase(workDao)
    }

    @Provides
    fun provideUpdateWorkTodoStateAsyncUseCase(
        workDao: WorkDao,
    ): UpdateWorkTodoStateAsyncUseCase {
        return IUpdateWorkTodoStateAsyncUseCase(workDao)
    }

    @Provides
    fun provideFetchAllWorkCommentsAsyncUseCaseAsyncUseCase(
        workCommentDao: WorkCommentDao,
    ): FetchAllWorkCommentsAsyncUseCase {
        return IFetchAllWorkCommentsAsyncUseCase(workCommentDao)
    }

    @Provides
    fun provideCreateWorkCommentAsyncUseCaseAsyncUseCase(
        workCommentDao: WorkCommentDao,
    ): CreateWorkCommentAsyncUseCase {
        return ICreateWorkCommentAsyncUseCase(workCommentDao)
    }

    @Provides
    fun provideUpdateWorkCommentAsyncUseCaseAsyncUseCase(
        workCommentDao: WorkCommentDao,
    ): UpdateWorkCommentAsyncUseCase {
        return IUpdateWorkCommentAsyncUseCase(workCommentDao)
    }
}