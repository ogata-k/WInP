package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.util.Optional

data class GetWorkInput(val workId: Long)

typealias GetWorkOutput = Result<Optional<Work>>

interface GetWorkAsyncUseCase : AsyncUseCase<GetWorkInput, GetWorkOutput>