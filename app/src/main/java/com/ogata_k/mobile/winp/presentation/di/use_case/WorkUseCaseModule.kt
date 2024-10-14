package com.ogata_k.mobile.winp.presentation.di.use_case

import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.ICreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IDeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IFetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IGetWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IUpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.presentation.use_case.work.IUpdateWorkTodoStateAsyncUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object WorkUseCaseModule {
    @Provides
    fun fetchPageWorksAsyncUseCase(): FetchPageWorksAsyncUseCase {
        return IFetchPageWorksAsyncUseCase()
    }

    @Provides
    fun getWorkAsyncUseCase(): GetWorkAsyncUseCase {
        return IGetWorkAsyncUseCase()
    }

    @Provides
    fun createWorkAsyncUseCase(): CreateWorkAsyncUseCase {
        return ICreateWorkAsyncUseCase()
    }

    @Provides
    fun updateWorkAsyncUseCase(): UpdateWorkAsyncUseCase {
        return IUpdateWorkAsyncUseCase()
    }

    @Provides
    fun deleteWorkAsyncUseCase(): DeleteWorkAsyncUseCase {
        return IDeleteWorkAsyncUseCase()
    }

    @Provides
    fun updateWorkTodoStateAsyncUseCase(): UpdateWorkTodoStateAsyncUseCase {
        return IUpdateWorkTodoStateAsyncUseCase()
    }

}