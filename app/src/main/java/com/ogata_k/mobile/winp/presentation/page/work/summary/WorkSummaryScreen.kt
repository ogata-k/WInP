package com.ogata_k.mobile.winp.presentation.page.work.summary

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSummaryScreen(navController: NavController, viewModel: WorkSummaryVM) {
    val uiState: WorkSummaryUiState by viewModel.uiStateFlow.collectAsState()
    val screenLoadingState = uiState.loadingState
    val basicScreenState = uiState.basicState

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.title_work_summary),
        navigationIcon = {
            AppBarBackButton(navController = navController)
        }
    ) { modifier, appBar ->

        // Eventの監視は必要だが監視するイベントがない

        Scaffold(
            modifier = modifier,
            topBar = appBar,
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                // TODO サマリーの選択期間を固定でヘッダーに表示
                // TODO サマリーの表示
            }
        }
    }
}