package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryOutput
import kotlin.coroutines.cancellation.CancellationException

class IGetSummaryWorkAsyncUseCase(private val dao: SummaryWorkDao) : GetSummaryAsyncUseCase {
    override suspend fun call(input: GetSummaryInput): GetSummaryOutput {
        return try {
            GetSummaryOutput.success(dao.getSummary(input.from, input.to))
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            GetSummaryOutput.failure(e)
        }
    }
}