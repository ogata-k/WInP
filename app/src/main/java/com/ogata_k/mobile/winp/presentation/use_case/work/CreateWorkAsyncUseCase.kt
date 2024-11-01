package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkOutput
import kotlinx.coroutines.CancellationException

class ICreateWorkAsyncUseCase(private val dao: WorkDao) : CreateWorkAsyncUseCase {
    override suspend fun call(input: CreateWorkInput): CreateWorkOutput {
        try {
            dao.insertWork(input.work)
            return CreateWorkOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return CreateWorkOutput.failure(e)
        }
    }
}