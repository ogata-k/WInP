package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.common.exception.NotFoundException
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkStateInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkStateOutput
import kotlin.coroutines.cancellation.CancellationException

class IUpdateWorkStateAsyncUseCase(private val dao: WorkDao) : UpdateWorkStateAsyncUseCase {
    override suspend fun call(input: UpdateWorkStateInput): UpdateWorkStateOutput {
        try {
            dao.updateWorkState(input.workId, input.completedAt)

            val newWorkOptional = dao.findWork(input.workId)
            if (!newWorkOptional.isPresent) {
                throw NotFoundException("work", input.workId)
            }
            return UpdateWorkStateOutput.success(newWorkOptional.get())
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkStateOutput.failure(e)
        }
    }
}