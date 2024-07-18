package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.model.wip.Work
import com.ogata_k.mobile.winp.presentation.widgert.common.PagingLoadColumn
import com.ogata_k.mobile.winp.presentation.widgert.work.WorkItem

@Composable
fun WorkIndexScreen(navController: NavController, viewModel: WorkIndexVM) {
    val uiState: WorkIndexUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // collect fLow and remember state and launch event
    val workPagingItems: LazyPagingItems<Work> = uiState.workPagingData.collectAsLazyPagingItems()


    PagingLoadColumn(
        contentPadding = PaddingValues(
            vertical = dimensionResource(id = R.dimen.padding_medium),
            horizontal = dimensionResource(id = R.dimen.padding_medium_large),
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium_large)),
        pagingItems = workPagingItems,
    ) { work ->
        WorkItem(work) { /*TODO*/ }
    }
}