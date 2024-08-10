package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.PagingData
import com.ogata_k.mobile.winp.presentation.model.work.Work
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class WorkIndexUiState(
    val isInSearchDate: Boolean,
    val searchDate: LocalDate,
    val workPagingData: Flow<PagingData<Work>>,
)