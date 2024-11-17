package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun SolidBorder(width: Dp, color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier = Modifier
            .background(color)
            .fillMaxWidth()
            .height(width)
            .then(modifier)
    )
}