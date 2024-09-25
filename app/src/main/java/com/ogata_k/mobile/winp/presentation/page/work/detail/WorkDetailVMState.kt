package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.model.common.UiLoadingState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.ToUiState
import java.util.Optional

data class WorkDetailVMState(
    val uiLoadingState: UiLoadingState,
    val workId: Int,
    val work: Optional<Work>,
    val inShowMoreAction: Boolean,
    val inConfirmDelete: Boolean,
) : ToUiState<WorkDetailUiState> {
    override fun toUiState(): WorkDetailUiState {
        return WorkDetailUiState(
            uiLoadingState = uiLoadingState,
            workId = workId,
            work = work,
            inShowMoreAction = inShowMoreAction,
            inConfirmDelete = inConfirmDelete,
        )
    }
}