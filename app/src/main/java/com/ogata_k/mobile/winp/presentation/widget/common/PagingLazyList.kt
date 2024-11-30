package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ogata_k.mobile.winp.R

/**
 * ページの差分読み込みを意識したLazyColumn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <Item : Any> PagingLoadColumn(
    pagingItems: LazyPagingItems<Item>,
    modifier: Modifier = Modifier,
    listModifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    useScrollbar: Boolean = true,
    key: ((item: Item) -> Any)? = null,
    contentType: ((item: Item) -> Any?)? = null,
    emptyBuilder: @Composable () -> Unit = {},
    loaderBuilder: @Composable () -> Unit = { DefaultCircularProgressIndicatorItem() },
    placeHolderBuilder: (@Composable () -> Unit)? = null,
    errorItemBuilder: @Composable (errorState: LoadState.Error) -> Unit = {
        DefaultErrorColumnItemBuilder(state = it)
    },
    headerBuilder: LazyListScope.(refreshLoadState: LoadState, isLoadedItemEmpty: Boolean) -> Unit = { _, _ -> },
    footerBuilder: LazyListScope.(refreshLoadState: LoadState, isLoadedItemEmpty: Boolean) -> Unit = { _, _ -> },
    itemBuilder: @Composable (item: Item) -> Unit,
) {
    Box(modifier = modifier) {
        LazyColumn(
            modifier = listModifier.fillMaxSize(),
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
        ) {
            buildPagingListItems(
                pagingItems = pagingItems,
                key = key,
                contentType = contentType,
                emptyBuilder = emptyBuilder,
                loaderBuilder = loaderBuilder,
                placeHolderBuilder = placeHolderBuilder,
                errorItemBuilder = errorItemBuilder,
                headerBuilder = headerBuilder,
                footerBuilder = footerBuilder,
                itemBuilder = itemBuilder,
            )
        }

        if (useScrollbar && pagingItems.itemSnapshotList.isNotEmpty()) {
            LazyColumnScrollBar(
                listState = state,
                isAlwaysShowScrollBar = false,
            )
        }
    }
}

/**
 * エラーアイテムのデフォルトビルダー
 */
@Composable
fun DefaultErrorColumnItemBuilder(state: LoadState.Error, modifier: Modifier = Modifier) {
    TitleMediumText(text = state.error.message ?: "UNKNOWN ERROR", modifier = modifier)
}

/**
 * ページの差分読み込みを意識したLazyColumn
 */
private fun <Item : Any> LazyListScope.buildPagingListItems(
    pagingItems: LazyPagingItems<Item>,
    key: ((item: Item) -> Any)? = null,
    contentType: ((item: Item) -> Any?)? = null,
    emptyBuilder: @Composable () -> Unit = {},
    loaderBuilder: @Composable () -> Unit = { DefaultCircularProgressIndicatorItem() },
    placeHolderBuilder: (@Composable () -> Unit)? = null,
    errorItemBuilder: @Composable (errorState: LoadState.Error) -> Unit,
    headerBuilder: LazyListScope.(refreshLoadState: LoadState, isLoadedItemEmpty: Boolean) -> Unit = { _, _ -> },
    footerBuilder: LazyListScope.(refreshLoadState: LoadState, isLoadedItemEmpty: Boolean) -> Unit = { _, _ -> },
    itemBuilder: @Composable (item: Item) -> Unit,
) {
    val loadStates = pagingItems.loadState.mediator ?: pagingItems.loadState.source
    val refreshLoadState = loadStates.refresh  // 更新時
    val prependLoadState = loadStates.prepend  // 前データを読み込む時
    val appendLoadState = loadStates.append    // 後データを読み込む時
    val hasError =
        refreshLoadState is LoadState.Error || prependLoadState is LoadState.Error || appendLoadState is LoadState.Error

    if (refreshLoadState is LoadState.Loading) {
        headerBuilder(refreshLoadState, true)
        item {
            loaderBuilder()
        }
        footerBuilder(refreshLoadState, true)
    } else {
        headerBuilder(refreshLoadState, pagingItems.itemSnapshotList.isEmpty())

        if (prependLoadState is LoadState.Loading && refreshLoadState is LoadState.NotLoading) {
            // 前データを読み込み中
            item {
                loaderBuilder()
            }
        }

        if (hasError && prependLoadState is LoadState.Error) {
            item {
                errorItemBuilder(prependLoadState)
            }
        }

        if (pagingItems.itemSnapshotList.isEmpty()) {
            if (!hasError) {
                item {
                    emptyBuilder()
                }
            }
        } else {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey(key),
                contentType = pagingItems.itemContentType(contentType)
            ) { index ->
                val item: Item? = pagingItems[index]
                item?.let {
                    itemBuilder(it)
                } ?: placeHolderBuilder?.let { builder ->
                    builder()
                }
            }
        }

        if (hasError && (refreshLoadState is LoadState.Error || appendLoadState is LoadState.Error)) {
            item {
                val error: LoadState.Error =
                    if (refreshLoadState is LoadState.Error) refreshLoadState else appendLoadState as LoadState.Error
                errorItemBuilder(error)
            }
        }

        if (appendLoadState is LoadState.Loading && refreshLoadState is LoadState.NotLoading) {
            // 後データを読み込み中
            item {
                loaderBuilder()
            }
        }
        footerBuilder(refreshLoadState, pagingItems.itemSnapshotList.isEmpty())
    }
}

/**
 * 差分読み込み中のローディング表示
 */
@Composable
fun DefaultCircularProgressIndicatorItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(dimensionResource(id = R.dimen.padding_medium))
    )
}