package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkOutput
import kotlin.coroutines.cancellation.CancellationException

class IGetWorkAsyncUseCase(private val dao: WorkDao) : GetWorkAsyncUseCase {
    override suspend fun call(input: GetWorkInput): GetWorkOutput {
        return try {
            GetWorkOutput.success(dao.findWork(input.workId))
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            GetWorkOutput.failure(e)
        }
    }
}