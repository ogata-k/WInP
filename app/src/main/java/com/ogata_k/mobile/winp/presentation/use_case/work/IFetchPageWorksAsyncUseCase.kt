package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.common.model.PageData
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksInput
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksOutput
import java.time.LocalTime
import kotlin.math.max
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork

class IFetchPageWorksAsyncUseCase(private val dao: WorkDao) : FetchPageWorksAsyncUseCase {
    override suspend fun call(input: FetchPageWorksInput): FetchPageWorksOutput {
        try {
            // ページ番号が1から始まり、ペース数が１ずつ増加するPageSourceを想定している為
            val itemOffset = max(input.currentPageNumber - 1, 0) * input.loadSize
            val from = LocalDateTimeConverter.toOffsetDateTime(input.searchDate.atStartOfDay())
            val to = LocalDateTimeConverter.toOffsetDateTime(input.searchDate.atTime(LocalTime.MAX))
            val items: MutableList<DomainWork> =
                dao.fetchPageWorksAtTheRange(from, to, itemOffset, input.loadSize + 1)
                    .toMutableList()
            var hasNextItem = false
            if (items.count() > input.loadSize) {
                // 一つだけ余分にとってきているので最後を取り除いて次があるものとして保存する
                items.dropLast(1)
                hasNextItem = true
            }
            return FetchPageWorksOutput(
                data = PageData.Succeeded(
                    items = items,
                    hasNextItem = hasNextItem
                )
            )
        } catch (e: Throwable) {
            return FetchPageWorksOutput(
                data = PageData.Failed(e)
            )
        }
    }
}