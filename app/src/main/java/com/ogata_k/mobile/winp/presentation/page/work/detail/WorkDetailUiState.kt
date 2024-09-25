package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.model.common.UiLoadingState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import java.util.Optional

data class WorkDetailUiState(
    val uiLoadingState: UiLoadingState,
    val workId: Int,
    val work: Optional<Work>,
    val inShowMoreAction: Boolean,
    val inConfirmDelete: Boolean,
)