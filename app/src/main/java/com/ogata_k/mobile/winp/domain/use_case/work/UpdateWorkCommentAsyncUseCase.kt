package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.WorkComment
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.OffsetDateTime

data class UpdateWorkCommentInput(
    val workId: Long,
    val workCommentId: Long,
    val comment: String,
    val modifiedAt: OffsetDateTime
)

typealias UpdateWorkCommentOutput = Result<List<WorkComment>>

interface UpdateWorkCommentAsyncUseCase :
    AsyncUseCase<UpdateWorkCommentInput, UpdateWorkCommentOutput>