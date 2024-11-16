package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentOutput
import kotlin.coroutines.cancellation.CancellationException

class IUpdateWorkCommentAsyncUseCase(private val dao: WorkCommentDao) :
    UpdateWorkCommentAsyncUseCase {
    override suspend fun call(input: UpdateWorkCommentInput): UpdateWorkCommentOutput {
        try {
            dao.updateWorkComment(input.workCommentId, input.comment, input.modifiedAt)

            return UpdateWorkCommentOutput.success(
                dao.fetchAllWorkCommentsOrderByCreatedAtDesc(
                    input.workId
                )
            )
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkCommentOutput.failure(e)
        }
    }
}