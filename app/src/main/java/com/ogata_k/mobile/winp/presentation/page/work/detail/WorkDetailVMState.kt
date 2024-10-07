package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.util.Optional

data class WorkDetailVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val workId: Int,
    val work: Optional<Work>,
    val inShowMoreAction: Boolean,
    val inConfirmDelete: Boolean,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkDetailUiState> {
    override fun toUiState(): WorkDetailUiState {
        return WorkDetailUiState(
            loadingState = loadingState,
            basicState = basicState,
            workId = workId,
            work = work,
            inShowMoreAction = inShowMoreAction,
            inConfirmDelete = inConfirmDelete,
        )
    }
}