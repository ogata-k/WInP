package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class GetWorkInput(val workId: Int)

typealias GetWorkOutput = Result<Work>

interface GetWorkAsyncUseCase : AsyncUseCase<GetWorkInput, GetWorkOutput>