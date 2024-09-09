package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.common.model.PageData
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksInput
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksOutput
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.max
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork

class IFetchPageWorksAsyncUseCase : FetchPageWorksAsyncUseCase {
    override suspend fun call(input: FetchPageWorksInput): FetchPageWorksOutput {
        //
        // @todo 実際の実装に置き換える
        //
        delay(1000)
        // Params for an initial load request or a refresh triggered by invalidate.
        // Or, Params to load a page to be appended to the end of the list.
        val currentPageNumber: Int = max(input.currentPageNumber, 1)

        val loadedItemCount: Int = (currentPageNumber - 1) * input.loadSize
        val searchDateTime: LocalDateTime = input.searchDate.atTime(LocalTime.now())

        val items = List(input.loadSize) {
            val workId = it + 1
            val itemIndex = loadedItemCount + it
            DomainWork(
                id = workId,
                title = "タスク$workId at the page $currentPageNumber at the index $itemIndex",
                description = "このタスクは${searchDateTime}におけるタスクです。タスクのIDは${workId}です。",
                beganAt = if (workId % 8 == 0) searchDateTime.minusSeconds((it * 2).toLong()) else null,
                endedAt = if (workId % 23 == 0) searchDateTime.minusSeconds(it.toLong())
                else if (workId % 7 == 0) searchDateTime.plusDays(2)
                else null,
                completedAt = if (workId % 3 == 0) LocalDateTime.now() else null,
                workTodos = emptyList(),
            )
        }

        return FetchPageWorksOutput(data = PageData.Succeeded(items = items, hasNextItem = true))
    }
}