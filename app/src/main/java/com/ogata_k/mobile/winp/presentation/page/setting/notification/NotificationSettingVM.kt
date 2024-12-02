package com.ogata_k.mobile.winp.presentation.page.setting.notification

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.toast.setting.NotFoundSetting
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.setting_form.NotificationSettingFormData
import com.ogata_k.mobile.winp.presentation.model.setting_form.NotificationSettingFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingVM @Inject constructor() :
    AbstractViewModel<ScreenLoadingState, NotificationSettingVMState, ScreenLoadingState, NotificationSettingUiState>() {
    override val viewModelStateFlow: MutableStateFlow<NotificationSettingVMState> =
        MutableStateFlow(
            NotificationSettingVMState(
                // 初期状態は未初期化状態とする
                loadingState = ScreenLoadingState.READY,
                basicState = BasicScreenState.initialState(),
                formData = NotificationSettingFormData(
                    todayTime = null,
                    tomorrowTime = null,
                ),
                validateExceptions = NotificationSettingFormValidateExceptions.empty(),
            )
        )

    override val uiStateFlow: StateFlow<NotificationSettingUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    override fun initializeVM() {
        var vmState = readVMState()
        if (vmState.loadingState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }

        vmState = vmState.copy(
            basicState = vmState.basicState.toDoingAction(),
        )
        updateVMState(vmState)

        // DBデータでFormの初期化をしたときに初期化を完了とする
        viewModelScope.launch {
            // TODO 実際の設定値取得処理が失敗した場合にifの中に入るようにする
            if (false) {
                val loadingState = ScreenLoadingState.NOT_FOUND_EXCEPTION
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                    )
                )
                EventBus.postToastEvent(NotFoundSetting())

                return@launch
            }

            // TODO 取得したフォームデータと置き換える
            val formData = NotificationSettingFormData(
                todayTime = null,
                tomorrowTime = null,
            )
            val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
            updateVMState(
                readVMState().copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    formData = formData,
                    validateExceptions = validateFormData(formData),
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
        vmState: NotificationSettingVMState,
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
        viewModelState: NotificationSettingVMState,
        basicScreenState: BasicScreenState
    ): NotificationSettingVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * 入力内容をバリデーションする
     */
    private fun validateFormData(formData: NotificationSettingFormData): NotificationSettingFormValidateExceptions {
        val todayTimeValidated: ValidationException = ValidationException.empty()
        val tomorrowTimeValidated: ValidationException = ValidationException.empty()

        return NotificationSettingFormValidateExceptions(
            todayTime = todayTimeValidated,
            tomorrowTime = tomorrowTimeValidated,
        )
    }
}