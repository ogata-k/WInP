package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.IUiState
import java.util.Optional

data class WorkDetailUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val workId: Long,
    val work: Optional<Work>,
    // 作成日時が直近->昔となるように並べられている
    val workComments: Result<List<WorkComment>>,
    val isInShowCommentForm: Boolean,
    val commentFormData: WorkCommentFormData,
    val validateCommentExceptions: WorkCommentFormValidateExceptions,
    val inShowMoreAction: Boolean,
    val inShowMoreCommentAction: Boolean,
    val inConfirmDelete: Boolean,
    // Not nullで表示中
    val inConfirmWorkTodoState: Long?,
) : IUiState<ScreenLoadingState>