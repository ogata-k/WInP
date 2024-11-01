package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkOutput
import kotlin.coroutines.cancellation.CancellationException

class IDeleteWorkAsyncUseCase(private val dao: WorkDao) : DeleteWorkAsyncUseCase {
    override suspend fun call(input: DeleteWorkInput): DeleteWorkOutput {
        try {
            dao.deleteWork(input.work)
            return DeleteWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return DeleteWorkOutput.failure(e)
        }
    }
}