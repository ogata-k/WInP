package com.ogata_k.mobile.winp.presentation.page.work.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkDetailScreen(navController: NavController, viewModel: WorkDetailVM) {
    val uiState: WorkDetailUiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    WithScaffoldSmallTopAppBar(
        text = null,
        navigationIcon = {
            AppBarBackButton(navController = navController) { uiState.screenState }
        },
        actions = {
            IconButton(
                onClick = {
                    // 編集画面への遷移
                    navController.navigate(WorkEditRouting(uiState.workId).toPath())
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = stringResource(
                        R.string.edit_work
                    ),
                )
            }
        },
    ) { modifier, appBar ->
        val screenScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->

            when (uiState.initializeState) {
                // 初期化中
                UiInitializeState.LOADING -> {
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
                UiInitializeState.INITIALIZED -> {
                    // 初期化が完了してエラーがない状態のはずなので、エラーを無視してgetして問題なし
                    val work: Work = uiState.work.get()
                    // TODO
                    Text("詳細画面", modifier = Modifier.padding(padding))

                    LaunchedEffect(UiNextScreenState.takeState(navController, false)) {
                        val nextScreenState = UiNextScreenState.takeState(navController, true)
                        screenScope.launch {
                            if (nextScreenState != null && nextScreenState.isDoneAction()) {
                                viewModel.updateNextScreenState(nextScreenState)
                            }
                        }
                    }
                }

                // アイテムが見つからず終了
                UiInitializeState.NOT_FOUND_EXCEPTION -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(R.string.failed_open_form_by_not_found_edit_target_task),
                        Toast.LENGTH_LONG
                    ).show()
                    // 画面POPの処理をLaunchedEffectで行わないと戻った先で値をハンドリングできない
                    LaunchedEffect(true) {
                        // 続いての処理はできないので前の画面に戻る
                        uiState.screenState.popWithSetState(
                            navController
                        )
                    }
                }

                // 予期せぬエラーがあった場合
                UiInitializeState.ERROR -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(R.string.failed_initialize_form),
                        Toast.LENGTH_LONG
                    ).show()
                    // 画面POPの処理をLaunchedEffectで行わないと戻った先で値をハンドリングできない
                    LaunchedEffect(true) {
                        // 続いての処理はできないので前の画面に戻る
                        uiState.screenState.popWithSetState(
                            navController
                        )
                    }
                }
            }
        }
    }
}