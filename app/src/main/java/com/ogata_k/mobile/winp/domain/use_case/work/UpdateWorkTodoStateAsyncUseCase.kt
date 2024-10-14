package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class UpdateWorkTodoStateInput(val work: Work, val workTodoId: Int)

typealias UpdateWorkTodoStateOutput = Result<Work>

interface UpdateWorkTodoStateAsyncUseCase :
    AsyncUseCase<UpdateWorkTodoStateInput, UpdateWorkTodoStateOutput>