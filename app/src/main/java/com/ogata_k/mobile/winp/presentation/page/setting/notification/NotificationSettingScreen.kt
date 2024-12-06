package com.ogata_k.mobile.winp.presentation.page.setting.notification

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
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
            // TODO 正しい表示に置き換える
            Text(
                uiState.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(padding)
            )
        }
    }
}