package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.ogata_k.mobile.winp.R

@Composable
fun Label(
    labelStatus: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
) {
    LabelSmallText(
        text = labelStatus,
        modifier = modifier
            .background(bgColor)
            .padding(dimensionResource(id = R.dimen.padding_extra_small)),
        color = textColor ?: colorResource(id = R.color.label_text),
        fontWeight = FontWeight.Bold,
    )
}