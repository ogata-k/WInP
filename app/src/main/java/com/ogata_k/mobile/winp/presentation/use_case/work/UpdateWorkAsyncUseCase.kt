package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkOutput
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class IUpdateWorkAsyncUseCase : UpdateWorkAsyncUseCase {
    override suspend fun call(input: UpdateWorkInput): UpdateWorkOutput {
        // @todo 実際の実装に置き換える
        try {
            delay(5000)
            val success = Random.nextInt() % 3 != 0
            if (!success) {
                throw RuntimeException("Fail Update work")
            }

            return UpdateWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpdateWorkOutput.failure(e)
        }
    }
}