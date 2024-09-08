package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.common.model.PageData
import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.LocalDate

/**
 * currentPageNumberは1オリジンで１づつ増加する
 */
data class FetchPageWorksInput(
    val currentPageNumber: Int,
    val loadSize: Int,
    val searchDate: LocalDate
)

data class FetchPageWorksOutput(val data: PageData<Work>)

interface FetchPageWorksAsyncUseCase : AsyncUseCase<FetchPageWorksInput, FetchPageWorksOutput>