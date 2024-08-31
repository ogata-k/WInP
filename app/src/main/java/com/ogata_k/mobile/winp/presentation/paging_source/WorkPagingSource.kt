package com.ogata_k.mobile.winp.presentation.paging_source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.max

/**
 * ページャーのデータソース
 * prevKey：取得したページの前ページのnextKey
 * nextKey：取得したデータの最後のタスクの作成日時
 */
class WorkPagingSource(searchDate: LocalDate? = null) : PagingSource<Int, Work>() {
    private var searchDate: LocalDate = searchDate ?: LocalDate.now()

    override fun getRefreshKey(state: PagingState<Int, Work>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Work> {
        try {
            // @todo DBを使った実装に置き換える
            delay(1000)
            // Params to load a page to the start of the list.
            if (params is LoadParams.Prepend) {
                throw IllegalStateException("Cannot fetch prepend data")
            }
            // Params for an initial load request or a refresh triggered by invalidate.
            // Or, Params to load a page to be appended to the end of the list.
            val currentPageNumber: Int = max(params.key ?: 1, 1)

            val loadedItemCount: Int = (currentPageNumber - 1) * params.loadSize
            val searchDate: LocalDateTime = this.searchDate.atStartOfDay()
            val items = List(params.loadSize) {
                val workId = it + 1
                val itemIndex = loadedItemCount + it
                Work(
                    id = workId,
                    title = "タスク$workId at the page $currentPageNumber at the index $itemIndex",
                    description = "このタスクは${this.searchDate}におけるタスクです。タスクのIDは${workId}です。",
                    beganAt = searchDate.plusSeconds(it.toLong()),
                    endedAt = null,
                    completedAt = null,
                    createdAt = searchDate.plusSeconds(it.toLong()),
                    updatedAt = searchDate.plusSeconds(it.toLong()),
                )
            }
            val result = LoadResult.Page(
                data = items,
                // 次の取得の際にここより前のデータを取得するために利用するキー
                // nullを指定することでここより前にデータはないことを表す
                prevKey = if (params is LoadParams.Refresh || currentPageNumber == 1) null else currentPageNumber - 1,
                // 次の取得の際にここより後のデータを取得するために利用するキー
                // nullを指定することでここより後にデータはないことを表す
                nextKey = currentPageNumber + 1,
            )
            Log.d(this.javaClass.toString(), result.toString())
            return result
        } catch (e: Exception) {
            val result = LoadResult.Error<Int, Work>(e)
            Log.d(this.javaClass.toString(), result.toString())
            return result
        }
    }
}