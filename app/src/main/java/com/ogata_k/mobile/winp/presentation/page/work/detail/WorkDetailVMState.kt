package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.ToUiState
import java.util.Optional

data class WorkDetailVMState(
    val initializeState: UiInitializeState,
    val screenState: UiNextScreenState,
    val workId: Int,
    val work: Optional<Work>,
) : ToUiState<WorkDetailUiState> {
    override fun toUiState(): WorkDetailUiState {
        return WorkDetailUiState(
            initializeState = initializeState,
            screenState = screenState,
            workId = workId,
            work = work,
        )
    }
}