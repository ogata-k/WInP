package com.ogata_k.mobile.winp.presentation.page.work.summary

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.SelectRangeDateType
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.WorkSummary
import com.ogata_k.mobile.winp.presentation.page.IUiState
import java.time.LocalDateTime

data class WorkSummaryUiState(
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
) : IUiState<ScreenLoadingState>