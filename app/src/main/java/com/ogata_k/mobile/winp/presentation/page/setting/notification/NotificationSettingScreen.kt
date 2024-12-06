package com.ogata_k.mobile.winp.presentation.page.setting.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.widget.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widget.common.WithScaffoldSmallTopAppBar

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
        val screenScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current

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
                    // TODO 初期化が完了したときに表示する画面

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
                    navController.popBackStack()
                }

                // 予期せぬエラーがあった場合
                ScreenLoadingState.ERROR -> {
                    // 続いての処理はできないので前の画面に戻る
                    // エラーの通知はトーストで行うので問題なし
                    navController.popBackStack()
                }
            }
        }
    }
}