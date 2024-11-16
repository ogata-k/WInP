package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.WorkComment
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class FetchAllWorkCommentsInput(val workId: Long)

typealias FetchAllWorkCommentsOutput = Result<List<WorkComment>>

interface FetchAllWorkCommentsAsyncUseCase :
    AsyncUseCase<FetchAllWorkCommentsInput, FetchAllWorkCommentsOutput>