package com.ogata_k.mobile.winp.presentation.page.setting.notification

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.common.type_converter.LocalTimeConverter
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RequestNotificationPermissionInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RequestNotificationPermissionSyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkInput
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.snackbar.setting.DoneSetting
import com.ogata_k.mobile.winp.presentation.event.snackbar.setting.FailedDeleteSetting
import com.ogata_k.mobile.winp.presentation.event.snackbar.setting.FailedUpdateSetting
import com.ogata_k.mobile.winp.presentation.event.toast.setting.NotFoundSetting
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class NotificationSettingVM @Inject constructor(
    private val getLocalNotificationUseCase: GetLocalNotificationAsyncUseCase,
    private val deleteLocalNotificationUseCase: DeleteLocalNotificationAsyncUseCase,
    private val upsertLocalNotificationUseCase: UpsertLocalNotificationAsyncUseCase,
    private val initializeAllNotificationChannelsUseCase: InitializeAllNotificationChannelsSyncUseCase,
    private val checkHasNotificationPermissionUseCase: CheckHasNotificationPermissionSyncUseCase,
    private val requestNotificationPermissionUseCase: RequestNotificationPermissionSyncUseCase,
    private val notifyForWorkUseCase: NotifyForWorkAsyncUseCase,
) :
    AbstractViewModel<ScreenLoadingState, NotificationSettingVMState, ScreenLoadingState, NotificationSettingUiState>() {
    override val viewModelStateFlow: MutableStateFlow<NotificationSettingVMState> =
        MutableStateFlow(
            NotificationSettingVMState(
                // 初期状態は未初期化状態とする
                loadingState = ScreenLoadingState.READY,
                basicState = BasicScreenState.initialState(),
                todayNotifyTime = null,
                needRequestTodayNotifyPermission = false,
                isInShowTodayTimePicker = false,
                isInShowClearTodayConfirmDialog = false,
                tomorrowNotifyTime = null,
                needRequestTomorrowNotifyPermission = false,
                isInShowTomorrowTimePicker = false,
                isInShowClearTomorrowConfirmDialog = false,
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

        viewModelScope.launch {
            // 通知するためにはチャネルが作成されないと通知できないのでここで初期化
            initializeAllNotificationChannelsUseCase.call(InitializeAllNotificationChannelsInput)

            val todayLocalNotificationResult =
                getLocalNotificationUseCase.call(GetLocalNotificationInput(LocalNotifyDiv.TODAY_EVERY_DAY))
            val tomorrowLocalNotificationResult =
                getLocalNotificationUseCase.call(GetLocalNotificationInput(LocalNotifyDiv.TOMORROW_EVERY_DAY))

            if (todayLocalNotificationResult.isFailure || tomorrowLocalNotificationResult.isFailure) {
                // 取得失敗。未設定の場合は成功はしているのでNotFoundではなくエラーとする
                val loadingState = ScreenLoadingState.ERROR
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                        todayNotifyTime = null,
                        tomorrowNotifyTime = null,
                    )
                )

                EventBus.postToastEvent(NotFoundSetting())
                return@launch
            }

            val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
            updateVMState(
                readVMState().copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    todayNotifyTime = todayLocalNotificationResult.getOrThrow().getOrNull()
                        ?.let { LocalTimeConverter.fromOffsetTime(it.notifyTime) },
                    tomorrowNotifyTime = tomorrowLocalNotificationResult.getOrThrow().getOrNull()
                        ?.let { LocalTimeConverter.fromOffsetTime(it.notifyTime) },
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
        optionalUpdater: (basicState: BasicScreenState) -> BasicScreenState = { it },
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
        basicScreenState: BasicScreenState,
    ): NotificationSettingVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * 本日の通知に関して通知権限リクエスト確認画面を閉じる
     */
    fun dismissTodayNotifyPermissionConfirmDialog() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        updateVMState(
            vmState.copy(
                needRequestTodayNotifyPermission = false,
            )
        )
    }

    /**
     * 明日の通知に関して通知権限リクエスト確認画面を閉じる
     */
    fun dismissTomorrowNotifyPermissionConfirmDialog() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        updateVMState(
            vmState.copy(
                needRequestTomorrowNotifyPermission = false,
            )
        )
    }

    /**
     * 本日の通知に関して通知権限をリクエストしてからリクエスト確認画面を閉じる
     */
    fun requestTodayNotifyPermissionAndDismissConfirmDialog() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        requestNotificationPermissionUseCase.call(RequestNotificationPermissionInput(LocalNotifyDiv.TODAY_EVERY_DAY))
        updateVMState(
            vmState.copy(
                needRequestTodayNotifyPermission = false,
            )
        )
    }

    /**
     * 明日の通知に関して通知権限をリクエストしてからリクエスト確認画面を閉じる
     */
    fun requestTomorrowNotifyPermissionAndDismissConfirmDialog() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        requestNotificationPermissionUseCase.call(RequestNotificationPermissionInput(LocalNotifyDiv.TOMORROW_EVERY_DAY))
        updateVMState(
            vmState.copy(
                needRequestTomorrowNotifyPermission = false,
            )
        )
    }

    /**
     * 本日の時間選択ダイアログを表示するかどうかを切り替える
     */
    fun showTodayTimePicker(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        if (show) {
            // 時間選択ダイアログを使って設定する前に通知権限がなければ通知できないので通知権限がなければリクエストさせる
            if (!checkHasNotificationPermissionUseCase.call(
                    CheckHasNotificationPermissionInput(
                        LocalNotifyDiv.TODAY_EVERY_DAY
                    )
                )
            ) {
                updateVMState(
                    vmState.copy(
                        needRequestTodayNotifyPermission = true,
                    )
                )
                return
            }
        }

        updateVMState(
            vmState.copy(
                isInShowTodayTimePicker = show,
            )
        )
    }

    /**
     * 翌日の時間選択ダイアログを表示するかどうかを切り替える
     */
    fun showTomorrowTimePicker(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら処理の必要はない
            return
        }

        if (show) {
            // 時間選択ダイアログを使って設定する前に通知権限がなければ通知できないので通知権限がなければリクエストさせる
            if (!checkHasNotificationPermissionUseCase.call(
                    CheckHasNotificationPermissionInput(
                        LocalNotifyDiv.TOMORROW_EVERY_DAY
                    )
                )
            ) {
                updateVMState(
                    vmState.copy(
                        needRequestTomorrowNotifyPermission = true,
                    )
                )
                return
            }
        }

        updateVMState(
            vmState.copy(
                isInShowTomorrowTimePicker = show,
            )
        )
    }

    /**
     * 本日の時間設定を削除するかどうかを確認するためのダイアログを表示するかどうかを切り替える
     */
    fun showClearTodayConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                isInShowClearTodayConfirmDialog = show,
            )
        )
    }

    /**
     * 翌日の時間設定を削除するかどうかを確認するためのダイアログを表示するかどうかを切り替える
     */
    fun showClearTomorrowConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                isInShowClearTomorrowConfirmDialog = show,
            )
        )
    }

    /**
     * 指定された通知区分の値を更新する。
     * 更新の成否にかかわらずダイアログなどを非表示にする。
     */
    private fun updateNotifyTimeAndDismissForTheDiv(
        notifyDiv: LocalNotifyDiv,
        time: LocalTime?,
        dismissUpdate: (vmState: NotificationSettingVMState) -> NotificationSettingVMState,
        updateTimeSetting: (vmState: NotificationSettingVMState, notifyTime: LocalTime?) -> NotificationSettingVMState,
    ) {
        val vmState = readVMState()
        if (!vmState.canLaunchAction()) {
            // エラーがある場合は作成や更新の処理を行わずに終了
            return
        }

        val newTempVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newTempVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Unit> = async(Dispatchers.IO + SupervisorJob()) {
                if (time == null) {
                    return@async deleteLocalNotificationUseCase.call(
                        DeleteLocalNotificationInput(notifyDiv)
                    )
                } else {
                    return@async upsertLocalNotificationUseCase.call(
                        UpsertLocalNotificationInput(
                            notifyDiv,
                            LocalTimeConverter.toOffsetTime(time)
                        )
                    )
                }
            }.await()

            val oldVmState = readVMState()
            val snackbarEvent = if (result.isSuccess) {
                if (time == null) DoneSetting(EventAction.DELETE) else DoneSetting(EventAction.UPDATE)
            } else {
                if (time == null) FailedDeleteSetting() else FailedUpdateSetting()
            }

            val newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptSnackbarEvent(snackbarEvent)
            val newVmState = oldVmState
                .copy(basicState = newBasicState)
                .let(dismissUpdate)
                .let {
                    if (result.isSuccess) {
                        updateTimeSetting(it, time)
                    } else {
                        it
                    }
                }

            // 実行結果を通知
            updateVMState(newVmState)
        }
    }

    /**
     * 本日の時間設定値を更新する。
     * 更新後すべての時間選択関連のPickerやDialogを非表示にする
     */
    fun updateTodayNotifyTimeAndDismiss(time: LocalTime?) {
        updateNotifyTimeAndDismissForTheDiv(
            notifyDiv = LocalNotifyDiv.TODAY_EVERY_DAY,
            time = time,
            dismissUpdate = { vmState ->
                vmState.copy(
                    isInShowTodayTimePicker = false,
                    isInShowClearTodayConfirmDialog = false,
                )
            },
            updateTimeSetting = { vmState, notifyTime -> vmState.copy(todayNotifyTime = notifyTime) },
        )
    }

    /**
     * 翌日の時間設定値を更新する。
     * 更新後すべての時間選択関連のPickerやDialogを非表示にする
     */
    fun updateTomorrowNotifyTimeAndDismiss(time: LocalTime?) {
        updateNotifyTimeAndDismissForTheDiv(
            notifyDiv = LocalNotifyDiv.TOMORROW_EVERY_DAY,
            time = time,
            dismissUpdate = { vmState ->
                vmState.copy(
                    isInShowTomorrowTimePicker = false,
                    isInShowClearTomorrowConfirmDialog = false,
                )
            },
            updateTimeSetting = { vmState, notifyTime -> vmState.copy(tomorrowNotifyTime = notifyTime) },
        )
    }
}