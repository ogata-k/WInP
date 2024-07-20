package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkIndexScreen(navController: NavController, viewModel: WorkIndexVM) {
    val uiState: WorkIndexUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // collect fLow and remember state and launch event
    val workPagingItems: LazyPagingItems<Work> = uiState.workPagingData.collectAsLazyPagingItems()

    // TODO PagingSourceをDBを使う形式に変更
    // TODO 表示する日時を変更することでリストを更新する機能

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.app_name),
        canChangeColor = false,
    ) { modifier, appBar ->
        Scaffold(
            modifier = modifier,
            topBar = appBar,
        ) { padding ->
            PagingLoadColumn(
                modifier = Modifier.padding(padding),
                pagingItems = workPagingItems,
                headerBuilder = { refreshLoadState, isLoadedItemEmpty ->
                    stickyHeader {
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.padding_large)),
                        ) {
                            // TODO ここで検索条件などを指定指定する。スクロールしたら消したい場合はこの上でitemなどで指定する
                            Text("hoge")
                        }
                    }
                }
            ) { work ->
                WorkItem(
                    work, modifier = Modifier.padding(
                        vertical = dimensionResource(id = R.dimen.padding_medium),
                        horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                    )
                ) {
                    // TODO onClick
                }
            }
        }
    }
}