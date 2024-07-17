package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.PagingData
import com.ogata_k.mobile.winp.presentation.model.wip.Work
import com.ogata_k.mobile.winp.presentation.page.ToUiState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkIndexVMState(val searchDate: LocalDate, val workPagingData: Flow<PagingData<Work>>) :
    ToUiState<WorkIndexUiState> {
    override fun toUiState(): WorkIndexUiState {
        return WorkIndexUiState(searchDate, workPagingData)
    }
}