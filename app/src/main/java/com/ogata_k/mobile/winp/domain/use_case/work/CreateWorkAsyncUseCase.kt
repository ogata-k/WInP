package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class CreateWorkInput(val work: Work)

typealias CreateWorkOutput = Result<Unit>

interface CreateWorkAsyncUseCase : AsyncUseCase<CreateWorkInput, CreateWorkOutput>