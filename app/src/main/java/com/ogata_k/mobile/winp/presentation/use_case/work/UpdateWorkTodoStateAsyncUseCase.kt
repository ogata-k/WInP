package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateOutput
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class IUpdateWorkTodoStateAsyncUseCase : UpdateWorkTodoStateAsyncUseCase {
    override suspend fun call(input: UpdateWorkTodoStateInput): UpdateWorkTodoStateOutput {
        // @todo 実際の実装に置き換える
        try {
            delay(3000)
            val success =
                input.work.workTodos.none { it.id == input.workTodoId } || Random.nextInt() % 3 != 0
            if (!success) {
                throw RuntimeException("Fail Update work todo state")
            }
            val newWork = input.work.copy(
                workTodos = input.work.workTodos.map {
                    if (it.id == input.workTodoId) {
                        it.copy(
                            completedAt = if (it.completedAt == null) LocalDateTime.now() else null,
                        )
                    } else {
                        it
                    }
                }
            )

            return UpdateWorkTodoStateOutput.success(newWork)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkTodoStateOutput.failure(e)
        }
    }
}