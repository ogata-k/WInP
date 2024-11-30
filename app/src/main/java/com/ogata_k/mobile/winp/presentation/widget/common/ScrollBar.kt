package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// 表示してから消えるまでの時間（ms）
private const val disableDelay: Long = 800

// Vertical方向のスクロールバーの横幅
private val scrollBarWidth = 8.dp

// スクロールバーの色
private val scrollBarColor = Color.Gray

/**
 * ScrollStateに連動したスクロールバーを表示するComposable
 *
 * https://qiita.com/yasukotelin/items/fcf5b538fac922cb08a5 を参考に作成
 *
 * 親のBoxの右端に表示されます。高さはコンテンツ量に応じて可変します。
 * スクロールできるだけのコンテンツがない場合は表示されません。
 *
 * @param isAlwaysShowScrollBar trueの場合はスクロールバーを常に表示します。
 * falseの場合はスクロール中のみ表示します。
 *
 * cf: https://qiita.com/takke/items/e717a2aae56691d1af08
 */
@Composable
fun BoxScope.LazyColumnScrollBar(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isAlwaysShowScrollBar: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(isAlwaysShowScrollBar) }

    LaunchedEffect(isAlwaysShowScrollBar, listState.isScrollInProgress) {
        isVisible = if (isAlwaysShowScrollBar || listState.isScrollInProgress) {
            true
        } else {
            delay(disableDelay) // スクロールが止まってからdisableDelay ms後に非表示にする
            false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Canvas(
            modifier = modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize()
        ) {
            val viewHeight = size.height
            val totalCount = listState.layoutInfo.totalItemsCount
            if (totalCount == 0) return@Canvas

            val firstVisibleItemIndex = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            val visibleItemCount = listState.layoutInfo.visibleItemsInfo.size

            val scrollRatio = firstVisibleItemIndex.toFloat() / totalCount

            // スクロールバーの位置とサイズを計算
            // @todo スクロールバーのサイズが確保しているアイテムのサイズで大きくなって小さくなってを繰り返してしまっているが致命的ではないのでとりあえず放置
            val scrollbarHeight = viewHeight * (visibleItemCount.toFloat() / totalCount)
            val scrollbarTopY1 = scrollRatio * viewHeight

            // 次のアイテムの位置とサイズを計算
            val scrollRatio2 = (firstVisibleItemIndex + 1).toFloat() / totalCount
            val scrollbarTopY2 = scrollRatio2 * viewHeight

            // 表示中の先頭アイテムの高さ
            val firstVisibleItemHeight = listState.layoutInfo.visibleItemsInfo.getOrNull(0)?.size

            // スクロールバー位置の微調整(スクロール量をスクロールバーのoffsetに変換する。offsetの範囲はこのアイテムと次のアイテムのスクロールバーの位置)
            val scrollbarTopOffset =
                if (firstVisibleItemHeight == null || firstVisibleItemHeight == 0) {
                    // 先頭アイテムの高さが不明なので微調整なし
                    0f
                } else {
                    firstVisibleItemScrollOffset.toFloat() / firstVisibleItemHeight * (scrollbarTopY2 - scrollbarTopY1)
                }

            // @todo FIXME ヘッダーが入ると描画位置がおかしくなる
            drawRect(
                color = scrollBarColor,
                topLeft = Offset(
                    size.width - scrollBarWidth.toPx(),
                    scrollbarTopY1 + scrollbarTopOffset
                ),
                size = Size(scrollBarWidth.toPx(), scrollbarHeight)
            )
        }
    }
}


/**
 * ScrollStateに連動したスクロールバーを表示するComposable
 *
 * 親のBoxの右端に表示されます。高さはコンテンツ量に応じて可変します。
 * スクロールできるだけのコンテンツがない場合は表示されません。
 *
 * @param isAlwaysShowScrollBar trueの場合はスクロールバーを常に表示します。
 * falseの場合はスクロール中のみ表示します。
 *
 * cf: https://qiita.com/yasukotelin/items/fcf5b538fac922cb08a5
 */
@Composable
fun BoxScope.ColumnScrollBar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    isAlwaysShowScrollBar: Boolean = false,
) {
    var isVisible by remember { mutableStateOf(isAlwaysShowScrollBar) }

    LaunchedEffect(isAlwaysShowScrollBar, scrollState.isScrollInProgress) {
        isVisible = if (isAlwaysShowScrollBar || scrollState.isScrollInProgress) {
            true
        } else {
            delay(disableDelay) // スクロールが止まってからdisableDelay ms後に非表示にする
            false
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Canvas(
            modifier = modifier
                .align(Alignment.CenterEnd)
                .fillMaxSize()
        ) {
            val totalScrollDistance = scrollState.maxValue.toFloat()
            val viewHeight = size.height
            val scrollRatio = scrollState.value.toFloat() / totalScrollDistance

            // スクロールバーの位置とサイズを計算
            val scrollbarHeight = viewHeight * (viewHeight / (totalScrollDistance + viewHeight))
            val scrollbarTopOffset = scrollRatio * (viewHeight - scrollbarHeight)

            drawRect(
                color = scrollBarColor,
                topLeft = Offset(size.width - scrollBarWidth.toPx(), scrollbarTopOffset),
                size = Size(scrollBarWidth.toPx(), scrollbarHeight)
            )
        }
    }
}