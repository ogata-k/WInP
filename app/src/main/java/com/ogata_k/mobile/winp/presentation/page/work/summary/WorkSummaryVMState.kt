package com.ogata_k.mobile.winp.presentation.page.work.summary

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.SelectRangeDateType
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.WorkSummary
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.time.LocalDateTime

data class WorkSummaryVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val rangeDateType: SelectRangeDateType,
    val summaryRangeFrom: LocalDateTime,
    val summaryRangeTo: LocalDateTime,
    val summaryData: WorkSummary,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkSummaryUiState> {
    override fun toUiState(): WorkSummaryUiState {
        return WorkSummaryUiState(
            loadingState = loadingState,
            basicState = basicState,
            rangeDateType = rangeDateType,
            summaryRangeFrom = summaryRangeFrom,
            summaryRangeTo = summaryRangeTo,
            summaryData = summaryData,
        )
    }
}