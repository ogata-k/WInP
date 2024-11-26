package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Summary
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.OffsetDateTime

data class GetSummaryInput(
    val from: OffsetDateTime,
    val to: OffsetDateTime,
)

typealias GetSummaryOutput = Result<Summary>

interface GetSummaryAsyncUseCase : AsyncUseCase<GetSummaryInput, GetSummaryOutput>