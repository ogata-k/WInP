package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ogata_k.mobile.winp.R

/**
 * ページの差分読み込みを意識したLazyColumn
 */
@Composable
fun <Item : Any> PagingLoadColumn(
    pagingItems: LazyPagingItems<Item>,
    modifier: Modifier = Modifier,
    key: ((item: Item) -> Any)? = null,
    contentType: ((item: Item) -> Any?)? = null,
    placeHolderBuilder: (@Composable () -> Unit)? = null,
    itemBuilder: @Composable (item: Item) -> Unit,
) {
    val loadStates = pagingItems.loadState.mediator ?: pagingItems.loadState.source
    val refreshLoadState = loadStates.refresh  // 更新時
    val prependLoadState = loadStates.prepend  // 前データを読み込む時
    val appendLoadState = loadStates.append    // 後データを読み込む時

    // @todo 必要ならscrollbarを表示するように修正
    // @todo 必要ならPullToRefreshを実装
    LazyColumn(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        if (refreshLoadState is LoadState.Loading) {
            item {
                CircularProgressIndicatorItem()
            }
        }

        if (prependLoadState is LoadState.Loading && refreshLoadState is LoadState.NotLoading) {
            // 前データを読み込み中
            item {
                CircularProgressIndicatorItem()
            }
        }

        if (refreshLoadState is LoadState.NotLoading) {
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

        if (refreshLoadState is LoadState.Error || prependLoadState is LoadState.Error || appendLoadState is LoadState.Error) {
            item {
                // @todo ほかの画面と同様のエラーハンドリング
                val error =
                    if (refreshLoadState is LoadState.Error) refreshLoadState else if (prependLoadState is LoadState.Error) prependLoadState else appendLoadState as LoadState.Error
                Row {
                    TitleMediumText(text = error.error.message ?: "UNKNOWN ERROR")
                }
            }
        }

        if (appendLoadState is LoadState.Loading && refreshLoadState is LoadState.NotLoading) {
            // 後データを読み込み中
            item {
                CircularProgressIndicatorItem()
            }
        }
    }
}

/**
 * 差分読み込み中のローディング表示
 */
@Composable
private fun CircularProgressIndicatorItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(dimensionResource(id = R.dimen.padding_large))
    )
}