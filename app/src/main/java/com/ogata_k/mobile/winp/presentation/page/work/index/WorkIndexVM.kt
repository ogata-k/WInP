package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.ogata_k.mobile.winp.domain.use_case.work.FetchPageWorksAsyncUseCase
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.FailedCreateWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.FailedDeleteWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.FailedUpdateWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.NotFoundWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededCreateWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededDeleteWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededUpdateWork
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import com.ogata_k.mobile.winp.presentation.paging_source.WorkPagingSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@HiltViewModel(assistedFactory = WorkIndexVM.WorkIndexVMFactory::class)
class WorkIndexVM @AssistedInject constructor(
    @Assisted private val initialSearchDate: LocalDate,
    private val fetchPageWorksUseCase: FetchPageWorksAsyncUseCase,
) : AbstractViewModel<ScreenLoadingState, WorkIndexVMState, ScreenLoadingState, WorkIndexUiState>() {
    @AssistedFactory
    interface WorkIndexVMFactory {
        fun create(initialSearchDate: LocalDate): WorkIndexVM
    }

    companion object {
        // 初回ページサイズ
        const val INITIAL_PAGE_SIZE: Int = 50

        // 差分のページサイズ
        const val PAGE_SIZE: Int = 50

        // 差分読み込み開始時のタイミング
        const val PREFETCH_DISTANCE: Int = 50
    }

    override val viewModelStateFlow: MutableStateFlow<WorkIndexVMState> =
        MutableStateFlow(WorkIndexVMState(
            // 最初から初期化済み。読み込みの状態を表現するのはPagerに任せる
            loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED,
            basicState = BasicScreenState.initialState()
                .updateInitialize(ScreenLoadingState.NO_ERROR_INITIALIZED),
            inShowMoreAction = false,
            isInSearchDate = false,
            searchDate = initialSearchDate,
            isInRefreshing = false,
            workPagingData = Pager(
                config = PagingConfig(
                    initialLoadSize = INITIAL_PAGE_SIZE,
                    pageSize = PAGE_SIZE,
                    prefetchDistance = PREFETCH_DISTANCE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    WorkPagingSource(
                        searchDate = readVMState().searchDate,
                        fetchPageWorksUseCase
                    )
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

    override fun initializeVM() {
        // None
    }

    override fun reloadVM() {
        // PagerのリロードはPagerに任せるので、画面のリロードは関係ない
    }

    override fun reloadVMWithConsumeEvent() {
        // PagerのリロードはPagerに任せるので、画面のリロードは関係ない
        consumeEvent()
    }

    override fun replaceVMBasicScreenState(
        viewModelState: WorkIndexVMState,
        basicScreenState: BasicScreenState,
    ): WorkIndexVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * Eventの監視
     * LaunchedEffect内で呼び出さないと何度も同じOwnerで監視してしまうので注意
     */
    fun listenEvent(
        screenLifecycle: LifecycleOwner,
        refreshListRequest: () -> Unit,
    ) {
        EventBus.onEvent<SucceededCreateWork>(screenLifecycle) {
            refreshListRequest()
        }
        EventBus.onEvent<FailedCreateWork>(screenLifecycle) {
            refreshListRequest()
        }

        EventBus.onEvent<SucceededUpdateWork>(screenLifecycle) {
            refreshListRequest()
        }
        EventBus.onEvent<FailedUpdateWork>(screenLifecycle) {
            refreshListRequest()
        }

        EventBus.onEvent<SucceededDeleteWork>(screenLifecycle) {
            refreshListRequest()
        }
        EventBus.onEvent<FailedDeleteWork>(screenLifecycle) {
            refreshListRequest()
        }

        EventBus.onEvent<NotFoundWork>(screenLifecycle) {
            refreshListRequest()
        }
    }

    /**
     * さらなる操作を要求するための操作一覧を表示するかどうかを切り替える
     */
    fun showMoreAction(show: Boolean = true) {
        val vmState = readVMState()
        if (vmState.inShowMoreAction || !show) {
            // 表示中なら非表示にする
            updateVMState(
                vmState.copy(
                    inShowMoreAction = show,
                )
            )
            return
        }

        updateVMState(
            vmState.copy(
                inShowMoreAction = true,
            )
        )
    }

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
            // 検索条件が変化していないのでリフレッシュはせずに終了
            val newVmState = vmState.copy(
                isInSearchDate = false,
                basicState = vmState.basicState.copy(needForceUpdate = false),
            )
            updateVMState(newVmState)
            return
        }

        val newVmState = vmState.copy(
            searchDate = date,
            isInSearchDate = false,
            basicState = vmState.basicState.copy(needForceUpdate = false),
        )
        updateVMState(newVmState)

        workPagingItems.refresh()
    }

    /**
     * リストのリフレッシュ処理のステータスを更新
     */
    fun updateListRefreshState(isInRefreshing: Boolean) {
        val vmState = readVMState()
        updateVMState(vmState.copy(isInRefreshing = isInRefreshing))
    }
}