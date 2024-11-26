package com.ogata_k.mobile.winp.presentation.page.work.summary

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryInput
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.SelectRangeDateType
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.toast.work.NotFoundWorkSummary
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.WorkSummary
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WorkSummaryVM @Inject constructor(
    private val getSummaryUseCase: GetSummaryAsyncUseCase,
) : AbstractViewModel<ScreenLoadingState, WorkSummaryVMState, ScreenLoadingState, WorkSummaryUiState>() {
    override val viewModelStateFlow: MutableStateFlow<WorkSummaryVMState> = MutableStateFlow(
        run {
            // 初期の選択状態は月～日
            val rangeDateType = SelectRangeDateType.ThisWeekMonToSun
            val rangeDate: Pair<LocalDate, LocalDate> =
                rangeDateType.getDefaultRange(LocalDate.now())
            val fromDateTime = rangeDate.first.atTime(LocalTime.MIN)
            val toDateTime = rangeDate.second.atTime(LocalTime.MAX)

            WorkSummaryVMState(
                // 初期状態は未初期化状態とする
                loadingState = ScreenLoadingState.READY,
                basicState = BasicScreenState.initialState(),
                rangeDateType = rangeDateType,
                summaryRangeFrom = fromDateTime,
                summaryRangeTo = toDateTime,
                summaryData = WorkSummary.empty(fromDateTime, toDateTime),
            )
        }
    )

    override val uiStateFlow: StateFlow<WorkSummaryUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    override fun initializeVM() {
        val vmState = readVMState()
        if (vmState.loadingState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }

        // サマリーで使うデータを集計する
        viewModelScope.launch {
            val summaryResult = getSummaryUseCase.call(
                GetSummaryInput(
                    LocalDateTimeConverter.toOffsetDateTime(vmState.summaryRangeFrom),
                    LocalDateTimeConverter.toOffsetDateTime(vmState.summaryRangeTo)
                )
            )

            if (summaryResult.isFailure) {
                val loadingState = ScreenLoadingState.NOT_FOUND_EXCEPTION
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                        summaryData = WorkSummary.empty(
                            vmState.summaryRangeFrom,
                            vmState.summaryRangeTo
                        ),
                    )
                )

                EventBus.postToastEvent(NotFoundWorkSummary())
                return@launch
            }

            val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
            updateVMState(
                readVMState().copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    summaryData = WorkSummary.fromDomainModel(summaryResult.getOrThrow()),
                )
            )
        }
    }

    override fun reloadVM() {
        reloadVMWithOptional(readVMState())
    }

    /**
     * アクションの実行結果を消費しつつVMをリロードする
     */
    override fun reloadVMWithConsumeEvent() {
        reloadVMWithOptional(readVMState()) { it.toConsumeSnackbarEvent() }
    }

    /**
     * 必要なら追加アクションを対応しつつVMをリロードする
     */
    private fun reloadVMWithOptional(
        vmState: WorkSummaryVMState,
        optionalUpdater: (basicState: BasicScreenState) -> BasicScreenState = { it }
    ) {
        if (readVMState().loadingState == ScreenLoadingState.READY) {
            // 初期化中相当の時は無視する
            return
        }

        val loadingState = ScreenLoadingState.READY
        updateVMState(
            vmState.copy(
                loadingState = loadingState,
                basicState = optionalUpdater(vmState.basicState.updateInitialize(loadingState)),
            )
        )

        // 初期化をそのまま呼び出す
        initializeVM()
    }

    override fun replaceVMBasicScreenState(
        viewModelState: WorkSummaryVMState,
        basicScreenState: BasicScreenState
    ): WorkSummaryVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * 選択期間の選択タイプを更新する
     */
    fun updateSelectRangeDateType(rangeDateType: SelectRangeDateType) {
        val vmState = readVMState()
        if (vmState.rangeDateType == rangeDateType) {
            // 更新されていないので処理をスキップ
            return
        }

        val rangeDate: Pair<LocalDate, LocalDate> = rangeDateType.getDefaultRange(LocalDate.now())
        updateRangeDateWithSelectRangeDateType(rangeDateType, rangeDate.first, rangeDate.second)
    }

    /**
     * 選択期間を選択タイプとともに更新する
     */
    private fun updateRangeDateWithSelectRangeDateType(
        rangeDateType: SelectRangeDateType,
        fromDate: LocalDate,
        toDate: LocalDate
    ) {
        val vmState = readVMState()

        val loadingState = ScreenLoadingState.READY
        updateVMState(
            vmState.copy(
                loadingState = loadingState,
                basicState = vmState.basicState.updateInitialize(loadingState),
                rangeDateType = rangeDateType,
                summaryRangeFrom = fromDate.atTime(LocalTime.MIN),
                summaryRangeTo = toDate.atTime(LocalTime.MAX),
            )
        )

        // 初期化をそのまま呼び出す
        initializeVM()
    }

    /**
     * 選択期間を更新する
     */
    fun updateRangeDate(fromDate: LocalDate, toDate: LocalDate) {
        val vmState = readVMState()
        updateRangeDateWithSelectRangeDateType(vmState.rangeDateType, fromDate, toDate)
    }
}