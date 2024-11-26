package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkOutput
import kotlin.coroutines.cancellation.CancellationException

class IUpdateWorkAsyncUseCase(private val dao: WorkDao) : UpdateWorkAsyncUseCase {
    override suspend fun call(input: UpdateWorkInput): UpdateWorkOutput {
        try {
            dao.updateWork(input.work)
            return UpdateWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkOutput.failure(e)
        }
    }
}