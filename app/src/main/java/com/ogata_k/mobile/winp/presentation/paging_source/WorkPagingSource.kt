package com.ogata_k.mobile.winp.presentation.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ogata_k.mobile.winp.common.model.PageData
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksInput
import com.ogata_k.mobile.winp.presentation.model.work.Work
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import kotlin.math.max

/**
 * ページャーのデータソース
 * prevKey：取得したページの前ページのnextKey
 * nextKey：取得したデータの最後のタスクの作成日時
 */
class WorkPagingSource(
    private val searchDate: LocalDate,
    private val fetchPageWorksUseCase: FetchPageWorksAsyncUseCase,
) : PagingSource<Int, Work>() {
    override fun getRefreshKey(state: PagingState<Int, Work>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Work> {
        try {
            // Params to load a page to the start of the list.
            if (params is LoadParams.Prepend) {
                throw IllegalStateException("Cannot fetch prepend data")
            }
            // Params for an initial load request or a refresh triggered by invalidate.
            // Or, Params to load a page to be appended to the end of the list.
            val currentPageNumber: Int = max(params.key ?: 1, 1)

            val loadSize = params.loadSize
            val searchDate: LocalDate = this.searchDate

            val fetchInput = FetchPageWorksInput(
                currentPageNumber = currentPageNumber,
                loadSize = loadSize,
                searchDate = searchDate,
            )
            val fetchDataResult = fetchPageWorksUseCase.call(fetchInput).data

            when (fetchDataResult) {
                is PageData.Failed -> {
                    return LoadResult.Error(fetchDataResult.exception)
                }

                is PageData.Succeeded -> {
                    return LoadResult.Page(
                        data = fetchDataResult.items.map { Work.fromDomainModel(it) },
                        // 次の取得の際にここより前のデータを取得するために利用するキー
                        // nullを指定することでここより前にデータはないことを表す
                        prevKey = if (params is LoadParams.Refresh || currentPageNumber == 1) null else currentPageNumber - 1,
                        // 次の取得の際にここより後のデータを取得するために利用するキー
                        // nullを指定することでここより後にデータはないことを表す
                        nextKey = if (fetchDataResult.hasNextItem) currentPageNumber + 1 else null,
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // データ取得で取得しきれなったエラーはここでキャッチする
            return LoadResult.Error<Int, Work>(e)
        }
    }
}