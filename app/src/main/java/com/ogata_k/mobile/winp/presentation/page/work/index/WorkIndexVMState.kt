package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.PagingData
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.ToUiState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkIndexVMState(
    // uiStateは常に初期化済みのうえ、アクション実行中もないので指定しない
    val isInSearchDate: Boolean,
    val searchDate: LocalDate,
    val workPagingData: Flow<PagingData<Work>>,
) :
    ToUiState<WorkIndexUiState> {
    override fun toUiState(): WorkIndexUiState {
        return WorkIndexUiState(isInSearchDate, searchDate, workPagingData)
    }
}