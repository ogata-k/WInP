package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.model.wip.Work
import com.ogata_k.mobile.winp.presentation.widgert.common.PagingLoadColumn
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.work.WorkItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkIndexScreen(navController: NavController, viewModel: WorkIndexVM) {
    val uiState: WorkIndexUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // collect fLow and remember state and launch event
    val workPagingItems: LazyPagingItems<Work> = uiState.workPagingData.collectAsLazyPagingItems()

    // TODO PagingSourceをDBを使う形式に変更
    // TODO 表示する日時を変更することでリストを更新する機能

    WithScaffoldSmallTopAppBar(text = stringResource(id = R.string.app_name)) { modifier, appBar ->
        Scaffold(
            modifier = modifier,
            topBar = { appBar() },
        ) { padding ->
            PagingLoadColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(
                    vertical = dimensionResource(id = R.dimen.padding_medium),
                    horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium_large)),
                pagingItems = workPagingItems,
            ) { work ->
                WorkItem(work) {
                    // TODO
                }
            }
        }
    }
}