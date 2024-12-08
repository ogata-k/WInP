package com.ogata_k.mobile.winp.presentation.page.setting.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.widget.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widget.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.ColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widget.common.ConfirmAlertDialog
import com.ogata_k.mobile.winp.presentation.widget.common.DialogOfTimePicker
import com.ogata_k.mobile.winp.presentation.widget.common.DisplaySmallText
import com.ogata_k.mobile.winp.presentation.widget.common.FormBlock
import com.ogata_k.mobile.winp.presentation.widget.common.WithScaffoldSmallTopAppBar
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingScreen(navController: NavController, viewModel: NotificationSettingVM) {
    val uiState: NotificationSettingUiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val screenLoadingState = uiState.loadingState
    val basicScreenState = uiState.basicState

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.setting_notification),
        navigationIcon = {
            AppBarBackButton(navController = navController)
        },
    ) { modifier, appBar ->
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->

            // 監視するEventがないので、Eventの監視はしていない

            when (screenLoadingState) {
                // 初期化中
                ScreenLoadingState.READY -> {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(dimensionResource(id = R.dimen.padding_large)),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                }

                // 初期化完了
                ScreenLoadingState.NO_ERROR_INITIALIZED -> {
                    val scrollState = rememberScrollState()

                    Box(modifier = Modifier.padding(padding)) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fillMaxSize()
                                .padding(dimensionResource(id = R.dimen.padding_large)),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                        ) {
                            TimeDisplayAndForm(
                                title = stringResource(R.string.today_work_notify_setting_title),
                                time = uiState.todayNotifyTime,
                                isInShowTimePicker = uiState.isInShowTodayTimePicker,
                                isInClearConfirmDialog = uiState.isInShowClearTodayConfirmDialog,
                                switchShowTimePicker = { viewModel.showTodayTimePicker(it) },
                                switchShowClearConfirmDialog = {
                                    viewModel.showClearTodayConfirmDialog(
                                        it
                                    )
                                },
                                updateTimeAndDismiss = {
                                    viewModel.updateTodayNotifyTimeAndDismiss(
                                        it
                                    )
                                },
                            )
                            Spacer(Modifier.height(dimensionResource(R.dimen.padding_extra_large)))
                            TimeDisplayAndForm(
                                title = stringResource(R.string.tomorrow_work_notify_setting_title),
                                time = uiState.tomorrowNotifyTime,
                                isInShowTimePicker = uiState.isInShowTomorrowTimePicker,
                                isInClearConfirmDialog = uiState.isInShowClearTomorrowConfirmDialog,
                                switchShowTimePicker = { viewModel.showTomorrowTimePicker(it) },
                                switchShowClearConfirmDialog = {
                                    viewModel.showClearTomorrowConfirmDialog(
                                        it
                                    )
                                },
                                updateTimeAndDismiss = {
                                    viewModel.updateTomorrowNotifyTimeAndDismiss(
                                        it
                                    )
                                },
                            )
                        }

                        ColumnScrollBar(
                            scrollState = scrollState,
                            isAlwaysShowScrollBar = false,
                        )
                    }

                    val event: SnackbarEvent? = uiState.peekSnackbarEvent()
                    if (event != null) {
                        val text = event.toMessage()
                        LaunchedEffect(event) {
                            if (event.getKind().isSucceeded()) {
                                // 対応項目更新
                                showSimpleSnackbar(snackbarHostState, text)

                                viewModel.consumeEvent()

                                if (basicScreenState.needForceUpdate) {
                                    viewModel.reloadVMWithConsumeEvent()
                                }
                            }
                        }
                    }
                }

                // アイテムが見つからず終了
                ScreenLoadingState.NOT_FOUND_EXCEPTION -> {
                    // 続いての処理はできないので前の画面に戻る
                    // エラーの通知はトーストで行うので問題なし
                    LaunchedEffect(Unit) {
                    navController.popBackStack()
                    }
                }

                // 予期せぬエラーがあった場合
                ScreenLoadingState.ERROR -> {
                    // 続いての処理はできないので前の画面に戻る
                    // エラーの通知はトーストで行うので問題なし
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeDisplayAndForm(
    title: String,
    time: LocalTime?,
    isInShowTimePicker: Boolean,
    isInClearConfirmDialog: Boolean,
    switchShowTimePicker: (toShow: Boolean) -> Unit,
    switchShowClearConfirmDialog: (toShow: Boolean) -> Unit,
    updateTimeAndDismiss: (time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FormBlock(
        title = title,
        errorMessage = null,
        isRequired = null,
    ) {
        Row(
            modifier = modifier.padding(start = dimensionResource(R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DisplaySmallText(
                formatFullTimeOrEmpty(time),
            )
            Spacer(modifier = Modifier.weight(1f))
            // 設定ボタン
            Button(
                onClick = { switchShowTimePicker(true) },
                colors = ButtonDefaults.buttonColors()
            ) { ButtonMediumText(text = stringResource(R.string.set_setting)) }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            // 取り消しボタン
            Button(
                onClick = { switchShowClearConfirmDialog(true) },
                colors = ButtonDefaults.filledTonalButtonColors(),
                enabled = time != null
            ) { ButtonMediumText(text = stringResource(R.string.clear_setting)) }
        }
    }
    if (isInShowTimePicker) {
        val (baseTimeHour, baseTimeMinute) = if (time == null) {
            // 特に深い理由はないが切りのいい時間をデフォルトで選択しておく
            val baseTime = LocalTime.now()
            var baseTimeHour = baseTime.hour
            var baseTimeMinute = baseTime.minute
            if (55 < baseTimeMinute) {
                baseTimeHour = (baseTimeHour + 1) % 24
                baseTimeMinute = 0
            } else if (baseTimeMinute % 5 != 0) {
                baseTimeMinute = (baseTimeMinute - (baseTimeMinute % 5) + 5) % 60
            }

            Pair(baseTimeHour, baseTimeMinute)
        } else {
            // すでに入力してある値を利用
            Pair(time.hour, time.minute)
        }

        val timePickerState = rememberTimePickerState(
            initialHour = baseTimeHour,
            initialMinute = baseTimeMinute,
            is24Hour = true,
        )
        DialogOfTimePicker(
            state = timePickerState,
            onDismissRequest = { /* ignore background dismiss */ },
            dismissButton = {
                TextButton(
                    onClick = {
                        switchShowTimePicker(false)
                    },
                    enabled = true,
                ) {
                    ButtonMediumText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        updateTimeAndDismiss(
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )
                    },
                    enabled = true,
                ) {
                    ButtonMediumText(text = stringResource(R.string.decide))
                }
            },
        )
    }

    if (isInClearConfirmDialog) {
        ConfirmAlertDialog(
            dialogTitle = stringResource(R.string.title_clear_time_setting),
            dialogText = stringResource(R.string.dialog_content_confirm_clear_time_setting),
            onDismissRequest = {
                switchShowClearConfirmDialog(false)
            },
            confirmButtonAction = Pair(
                stringResource(R.string.clear_setting)
            ) {
                updateTimeAndDismiss(null)
            },
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            confirmActionIsDanger = true,
            enabledButtons = true,
        )
    }
}