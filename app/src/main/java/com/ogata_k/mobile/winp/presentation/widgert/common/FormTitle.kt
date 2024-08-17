package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FormTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    TitleLargeText(
        text = title,
        modifier = modifier,
    )
}
