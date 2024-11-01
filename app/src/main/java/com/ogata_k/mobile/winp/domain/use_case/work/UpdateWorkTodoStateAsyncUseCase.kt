package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.OffsetDateTime

data class UpdateWorkTodoStateInput(
    val workId: Long,
    val workTodoId: Long,
    val completedAt: OffsetDateTime?
)

typealias UpdateWorkTodoStateOutput = Result<Work>

interface UpdateWorkTodoStateAsyncUseCase :
    AsyncUseCase<UpdateWorkTodoStateInput, UpdateWorkTodoStateOutput>