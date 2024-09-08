package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkOutput
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.random.Random

class ICreateWorkAsyncUseCase : CreateWorkAsyncUseCase {
    override suspend fun call(input: CreateWorkInput): CreateWorkOutput {
        // @todo 実際の実装に置き換える
        try {
            delay(5000)
            val success = Random.nextInt() % 3 != 0
            if (!success) {
                throw RuntimeException("Fail Create work")
            }

            return CreateWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return CreateWorkOutput.failure(e)
        }
    }
}