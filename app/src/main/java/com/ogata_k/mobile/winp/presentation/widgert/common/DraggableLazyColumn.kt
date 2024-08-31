package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.channels.Channel

// ref https://medium.com/@artemsi93/reordering-list-via-drag-n-drop-in-jetpack-compose-cfb8c63ccf9b

/**
 * ドラッグ可能なColumn内のアイテムContentType
 */
data class DraggableColumnItemContentType(val index: Int)

/**
 * LazyColumnの中でドラッグ＆ドロップによって並び替えを行いたいときに利用する。
 * 一つのLazyColumnの中で連続した一つのアイテム群であることを想定している。
 * items()やitemsIndexed()などの代わりに指定する。
 */
inline fun <T : Any> LazyListScope.draggableColumnItems(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> Any)? = null,
    dragDropState: DragDropColumnState,
    crossinline content: @Composable LazyItemScope.(Modifier, T) -> Unit,
) {
    itemsIndexed(
        items = items,
        key = key,
        contentType = { index, _ -> DraggableColumnItemContentType(index = index) })
    { index, item ->
        val modifier = if (dragDropState.draggingItemIndex == index) {
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationY = dragDropState.delta
                }
        } else {
            Modifier
        }

        content(modifier, item)
    }
}

/**
 * LazyColumnの中にある[draggableColumnItems]で構築されたアイテムのコンポーザブルを対象にして、
 * 長押しによるドラッグ＆ドロップができるようにコンテナ化する。
 *
 * ※ 基本的にLazyColumnのModifierの中で一番最後に指定する。
 */
fun Modifier.draggableColumnContainer(
    dragDropState: DragDropColumnState,
    canDrag: Boolean = true
): Modifier {
    if (!canDrag) {
        return this
    }

    return this.then(
        pointerInput(dragDropState) {
            detectDragGesturesAfterLongPress(
                onDrag = { change, offset ->
                    change.consume()
                    dragDropState.onDrag(offset = offset)
                },
                onDragStart = { offset -> dragDropState.onDragStart(offset = offset) },
                onDragEnd = { dragDropState.onDragInterrupted() },
                onDragCancel = { dragDropState.onDragInterrupted() }
            )
        }
    )
}

/**
 * ドラッグ＆ドロップできるColumnのState
 */
class DragDropColumnState(
    private val draggableItemsNum: Int,
    private val stateList: LazyListState,
    private val onMove: (Int, Int) -> Unit,
) {
    var draggingItemIndex: Int? by mutableStateOf(null)

    var delta by mutableFloatStateOf(0f)

    val scrollChannel = Channel<Float>()

    private var draggingItem: LazyListItemInfo? = null

    internal fun onDragStart(offset: Offset) {
        stateList.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                (it.contentType as? DraggableColumnItemContentType)?.let { draggableItem ->
                    draggingItem = it
                    draggingItemIndex = draggableItem.index
                }
            }
    }

    internal fun onDragInterrupted() {
        draggingItem = null
        draggingItemIndex = null
        delta = 0f
    }

    internal fun onDrag(offset: Offset) {
        delta += offset.y

        val currentDraggingItemIndex =
            draggingItemIndex ?: return
        val currentDraggingItem =
            draggingItem ?: return

        val startOffset = currentDraggingItem.offset + delta
        val endOffset =
            currentDraggingItem.offset + currentDraggingItem.size + delta
        val middleOffset = startOffset + (endOffset - startOffset) / 2

        val targetItem =
            stateList.layoutInfo.visibleItemsInfo.find { item ->
                middleOffset.toInt() in item.offset..item.offset + item.size &&
                        currentDraggingItem.index != item.index &&
                        item.contentType is DraggableColumnItemContentType
            }

        if (targetItem != null) {
            val targetIndex = (targetItem.contentType as DraggableColumnItemContentType).index
            onMove(currentDraggingItemIndex, targetIndex)
            draggingItemIndex = targetIndex
            delta += currentDraggingItem.offset - targetItem.offset
            draggingItem = targetItem
        } else {
            val startOffsetToTop = startOffset - stateList.layoutInfo.viewportStartOffset
            val endOffsetToBottom = endOffset - stateList.layoutInfo.viewportEndOffset
            val scroll =
                when {
                    startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                    endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                    else -> 0f
                }

            if (scroll != 0f && currentDraggingItemIndex != 0 && currentDraggingItemIndex != draggableItemsNum - 1) {
                scrollChannel.trySend(scroll)
            }
        }
    }
}

@Composable
fun rememberDragDropColumnState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit,
    draggableItemsNum: Int
): DragDropColumnState {
    val state =
        remember(lazyListState) {
            DragDropColumnState(
                draggableItemsNum = draggableItemsNum,
                stateList = lazyListState,
                onMove = onMove,
            )
        }

    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}
