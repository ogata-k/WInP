package com.ogata_k.mobile.winp.presentation.page.work.detail

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.util.Optional

data class WorkDetailVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    /**
     * 現在の画面を強制的に閉じてほしいときに指定する。ほかのフラグとの競合に注意。
     */
    val needForcePopThisScreen: Boolean,
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
    val inConfirmCopy: Boolean,
    // Not nullで表示中
    val inConfirmWorkTodoState: Long?,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkDetailUiState> {
    override fun toUiState(): WorkDetailUiState {
        return WorkDetailUiState(
            loadingState = loadingState,
            basicState = basicState,
            needForcePopThisScreen = needForcePopThisScreen,
            workId = workId,
            work = work,
            workComments = workComments,
            isInShowCommentForm = isInShowCommentForm,
            commentFormData = commentFormData,
            validateCommentExceptions = validateCommentExceptions,
            inShowMoreAction = inShowMoreAction,
            inShowMoreCommentAction = inShowMoreCommentAction,
            inConfirmDelete = inConfirmDelete,
            inConfirmCopy = inConfirmCopy,
            inConfirmWorkTodoState = inConfirmWorkTodoState,
        )
    }
}