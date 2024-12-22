package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.OffsetDateTime

data class UpdateWorkStateInput(
    val workId: Long,
    val completedAt: OffsetDateTime?,
)

typealias UpdateWorkStateOutput = Result<Work>

interface UpdateWorkStateAsyncUseCase :
    AsyncUseCase<UpdateWorkStateInput, UpdateWorkStateOutput>