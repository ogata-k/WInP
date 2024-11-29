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
    val isInSelectRangeDateType: Boolean,
    val rangeDateType: SelectRangeDateType,
    val isInShowRangeDatePicker: Boolean,
    val summaryRangeFrom: LocalDateTime,
    val summaryRangeTo: LocalDateTime,
    val summaryData: WorkSummary,
    val isUncompletedWorkExpanded: Boolean,
    val isExpiredUncompletedWorkExpanded: Boolean,
    val isCompletedWorkExpanded: Boolean,
    val isExpiredCompletedWorkExpanded: Boolean,
    val isPostedCommentExpanded: Boolean,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkSummaryUiState> {
    override fun toUiState(): WorkSummaryUiState {
        return WorkSummaryUiState(
            loadingState = loadingState,
            basicState = basicState,
            isInSelectRangeDateType = isInSelectRangeDateType,
            rangeDateType = rangeDateType,
            isInShowRangeDatePicker = isInShowRangeDatePicker,
            summaryRangeFrom = summaryRangeFrom,
            summaryRangeTo = summaryRangeTo,
            summaryData = summaryData,
            isUncompletedWorkExpanded = isUncompletedWorkExpanded,
            isExpiredUncompletedWorkExpanded = isExpiredUncompletedWorkExpanded,
            isCompletedWorkExpanded = isCompletedWorkExpanded,
            isExpiredCompletedWorkExpanded = isExpiredCompletedWorkExpanded,
            isPostedCommentExpanded = isPostedCommentExpanded,
        )
    }
}