package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.common.exception.NotFoundException
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateOutput
import kotlin.coroutines.cancellation.CancellationException

class IUpdateWorkTodoStateAsyncUseCase(private val dao: WorkDao) : UpdateWorkTodoStateAsyncUseCase {
    override suspend fun call(input: UpdateWorkTodoStateInput): UpdateWorkTodoStateOutput {
        try {
            dao.updateTaskState(input.workTodoId, input.completedAt)

            val newWorkOptional = dao.findWork(input.workId)
            if (!newWorkOptional.isPresent) {
                throw NotFoundException("work", input.workId)
            }
            return UpdateWorkTodoStateOutput.success(newWorkOptional.get())
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkTodoStateOutput.failure(e)
        }
    }
}