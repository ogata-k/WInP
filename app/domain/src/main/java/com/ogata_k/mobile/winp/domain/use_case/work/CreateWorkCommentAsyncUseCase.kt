package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.WorkComment
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class CreateWorkCommentInput(val workComment: WorkComment)

typealias CreateWorkCommentOutput = Result<Unit>

interface CreateWorkCommentAsyncUseCase :
    AsyncUseCase<CreateWorkCommentInput, CreateWorkCommentOutput>