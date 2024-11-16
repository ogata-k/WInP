package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentInput
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentOutput
import kotlin.coroutines.cancellation.CancellationException

class ICreateWorkCommentAsyncUseCase(private val dao: WorkCommentDao) :
    CreateWorkCommentAsyncUseCase {
    override suspend fun call(input: CreateWorkCommentInput): CreateWorkCommentOutput {
        try {
            dao.insertWorkComment(input.workComment)
            return CreateWorkCommentOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return CreateWorkCommentOutput.failure(e)
        }
    }
}