package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import com.ogata_k.mobile.winp.presentation.paging_source.WorkPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkIndexVM @Inject constructor() : AbstractViewModel<WorkIndexVMState, WorkIndexUiState>(
    initialViewModelState = { viewModelScope ->
        WorkIndexVMState(
            searchDate = LocalDate.now(),
            workPagingData = Pager(
                config = PagingConfig(
                    initialLoadSize = INITIAL_PAGE_SIZE,
                    pageSize = PAGE_SIZE,
                    prefetchDistance = PREFETCH_DISTANCE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    // @todo UseCaseなどから呼び出して取得する
                    WorkPagingSource()
                }
            )
                .flow
                // Hot Flowであるページャーのフローを必要になったときに呼び出せるCold Flowに変換して
                // 監視が必要なスコープ内でだけで管理したいのでstateInやsharedInの代わりにcachedInを使う
                .cachedIn(scope = viewModelScope)
        )
    }
) {
    companion object {
        // 初回ページサイズ
        const val INITIAL_PAGE_SIZE: Int = 100

        // 差分のページサイズ
        const val PAGE_SIZE: Int = 50

        // 差分読み込み開始時のタイミング
        const val PREFETCH_DISTANCE: Int = 50
    }
}