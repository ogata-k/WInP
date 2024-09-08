package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import java.util.Optional

data class WorkDetailUiState(
    val initializeState: UiInitializeState,
    val screenState: UiNextScreenState,
    val workId: Int,
    val work: Optional<Work>,
)