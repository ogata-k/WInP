package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkCommentInput
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkCommentOutput
import kotlin.coroutines.cancellation.CancellationException

class IDeleteWorkCommentAsyncUseCase(private val dao: WorkCommentDao) :
    DeleteWorkCommentAsyncUseCase {
    override suspend fun call(input: DeleteWorkCommentInput): DeleteWorkCommentOutput {
        try {
            dao.deleteWorkComment(input.workComment)
            return DeleteWorkCommentOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return DeleteWorkCommentOutput.failure(e)
        }
    }
}