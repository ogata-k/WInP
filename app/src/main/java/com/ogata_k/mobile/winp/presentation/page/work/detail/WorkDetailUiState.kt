package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.page.IUiState
import java.util.Optional

data class WorkDetailUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val workId: Long,
    val work: Optional<Work>,
    val workComments: Result<List<WorkComment>>,
    val inShowMoreAction: Boolean,
    val inConfirmDelete: Boolean,
    // Not nullで表示中
    val inConfirmWorkTodoState: Long?,
) : IUiState<ScreenLoadingState>