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
    // 時刻まで見る必要がないので、時差を考慮しないローカルな方を使う
    val searchDate: LocalDate
)

data class FetchPageWorksOutput(val data: PageData<Work>)

/**
 * ページ番号が1から始まり、ペース数が１ずつ増加するPageSourceを想定して取得するユースケース
 */
interface FetchPageWorksAsyncUseCase : AsyncUseCase<FetchPageWorksInput, FetchPageWorksOutput>