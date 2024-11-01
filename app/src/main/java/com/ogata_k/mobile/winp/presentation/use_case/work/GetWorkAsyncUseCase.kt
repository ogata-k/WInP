package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkOutput

class IGetWorkAsyncUseCase(private val dao: WorkDao) : GetWorkAsyncUseCase {
    override suspend fun call(input: GetWorkInput): GetWorkOutput {
        return dao.findWork(input.workId)
    }
}