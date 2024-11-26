package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.PagingData
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.IUiState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkIndexUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val inShowMoreAction: Boolean,
    val isInSearchDate: Boolean,
    val searchDate: LocalDate,
    val isInRefreshing: Boolean,
    val workPagingData: Flow<PagingData<Work>>,
) : IUiState<ScreenLoadingState>