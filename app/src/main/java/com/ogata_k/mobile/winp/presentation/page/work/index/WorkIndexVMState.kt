package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.PagingData
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.IVMState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkIndexVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    // uiStateは常に初期化済みのうえ、アクション実行中もないので指定しない
    val isInSearchDate: Boolean,
    val searchDate: LocalDate,
    val isInRefreshing: Boolean,
    val workPagingData: Flow<PagingData<Work>>,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkIndexUiState> {
    override fun toUiState(): WorkIndexUiState {
        return WorkIndexUiState(
            loadingState,
            basicState,
            isInSearchDate,
            searchDate,
            isInRefreshing,
            workPagingData
        )
    }
}