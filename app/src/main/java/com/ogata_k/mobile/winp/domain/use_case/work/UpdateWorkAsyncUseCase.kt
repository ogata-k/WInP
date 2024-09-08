package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class UpdateWorkInput(val work: Work)

typealias UpdateWorkOutput = Result<Unit>

interface UpdateWorkAsyncUseCase : AsyncUseCase<UpdateWorkInput, UpdateWorkOutput>