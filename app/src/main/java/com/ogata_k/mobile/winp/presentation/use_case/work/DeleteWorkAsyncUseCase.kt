package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkOutput
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class IDeleteWorkAsyncUseCase : DeleteWorkAsyncUseCase {
    override suspend fun call(input: DeleteWorkInput): DeleteWorkOutput {
        // @todo 実際の実装に置き換える
        try {
            delay(5000)
            val success = Random.nextInt() % 3 != 0
            if (!success) {
                throw RuntimeException("Fail Delete work")
            }

            return DeleteWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return DeleteWorkOutput.failure(e)
        }
    }
}