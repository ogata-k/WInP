package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class DeleteWorkInput(val work: Work)

typealias DeleteWorkOutput = Result<Unit>

interface DeleteWorkAsyncUseCase : AsyncUseCase<DeleteWorkInput, DeleteWorkOutput>