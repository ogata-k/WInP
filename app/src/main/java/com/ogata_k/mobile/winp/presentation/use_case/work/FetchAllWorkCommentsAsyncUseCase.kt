package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkCommentDao
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsInput
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsOutput
import kotlin.coroutines.cancellation.CancellationException

class IFetchAllWorkCommentsAsyncUseCase(private val dao: WorkCommentDao) :
    FetchAllWorkCommentsAsyncUseCase {
    override suspend fun call(input: FetchAllWorkCommentsInput): FetchAllWorkCommentsOutput {
        return try {
            FetchAllWorkCommentsOutput.success(dao.fetchAllWorkCommentsOrderByCreatedAtDesc(input.workId))
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            FetchAllWorkCommentsOutput.failure(e)
        }
    }
}