package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.WorkComment
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class DeleteWorkCommentInput(val workComment: WorkComment)

typealias DeleteWorkCommentOutput = Result<Unit>


interface DeleteWorkCommentAsyncUseCase :
    AsyncUseCase<DeleteWorkCommentInput, DeleteWorkCommentOutput>