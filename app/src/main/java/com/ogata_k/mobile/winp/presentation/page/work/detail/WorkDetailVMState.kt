package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.util.Optional

data class WorkDetailVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val workId: Long,
    val work: Optional<Work>,
    val workComments: Result<List<WorkComment>>,
    val inShowMoreAction: Boolean,
    val inConfirmDelete: Boolean,
    // Not nullで表示中
    val inConfirmWorkTodoState: Long?,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkDetailUiState> {
    override fun toUiState(): WorkDetailUiState {
        return WorkDetailUiState(
            loadingState = loadingState,
            basicState = basicState,
            workId = workId,
            work = work,
            workComments = workComments,
            inShowMoreAction = inShowMoreAction,
            inConfirmDelete = inConfirmDelete,
            inConfirmWorkTodoState = inConfirmWorkTodoState,
        )
    }
}