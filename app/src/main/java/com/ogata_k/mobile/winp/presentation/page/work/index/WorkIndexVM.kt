package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import com.ogata_k.mobile.winp.presentation.paging_source.WorkPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkIndexVM @Inject constructor() : AbstractViewModel<WorkIndexVMState, WorkIndexUiState>() {
    companion object {
        // 初回ページサイズ
        const val INITIAL_PAGE_SIZE: Int = 100

        // 差分のページサイズ
        const val PAGE_SIZE: Int = 50

        // 差分読み込み開始時のタイミング
        const val PREFETCH_DISTANCE: Int = 50
    }

    override val viewModelStateFlow: MutableStateFlow<WorkIndexVMState> =
        MutableStateFlow(WorkIndexVMState(
            isInSearchDate = false,
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
                    WorkPagingSource(searchDate = readVMState().searchDate)
                }
            )
                .flow
                // Hot Flowであるページャーのフローを必要になったときに呼び出せるCold Flowに変換して
                // 監視が必要なスコープ内でだけで管理したいのでstateInやsharedInの代わりにcachedInを使う
                .cachedIn(scope = viewModelScope)
        )
        )
    override val uiStateFlow: StateFlow<WorkIndexUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * 日にち検索ダイアログを表示(falseなら非表示化)
     */
    fun showDatePickerForSearch(show: Boolean = true) {
        val vmState = readVMState()
        val newVmState = vmState.copy(isInSearchDate = show)
        updateVMState(newVmState)
    }

    /**
     * 検索条件の適用
     */
    fun updateAndHideDialogSearchQuery(workPagingItems: LazyPagingItems<Work>, date: LocalDate) {
        val vmState = readVMState()
        if (vmState.searchDate == date) {
            // 検索条件が変化していないのでスキップ
            val newVmState = vmState.copy(isInSearchDate = false)
            updateVMState(newVmState)
            return
        }

        val newVmState = vmState.copy(searchDate = date, isInSearchDate = false)
        updateVMState(newVmState)

        workPagingItems.refresh()
    }
}