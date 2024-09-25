package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R

@Composable
fun DropdownMenuButton(
    expanded: Boolean,
    showMoreAction: (show: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dropdownAlignment: Alignment = Alignment.TopStart,
    menuContent: @Composable (ColumnScope.() -> Unit),
) {
    Box(modifier = Modifier.wrapContentSize(dropdownAlignment)) {
        IconButton(
            modifier = modifier,
            onClick = {
                showMoreAction(true)
            },
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(
                    R.string.menu_have_more_action
                ),
            )
        }
        DropdownMenu(
            expanded = expanded,
            // メニューの外がタップされた時に閉じる
            onDismissRequest = { showMoreAction(false) },
            content = menuContent,
        )
    }
}
