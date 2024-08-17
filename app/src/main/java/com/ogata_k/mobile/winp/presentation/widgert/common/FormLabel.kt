package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R

@Composable
fun FormLabel(
    isRequired: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isRequired) {
        RequireLabel()
    } else {
        OptionalLabel()
    }
}

@Composable
private fun RequireLabel(
    modifier: Modifier = Modifier
) {
    LabelSmallText(
        text = stringResource(R.string.required),
        modifier = modifier
            .background(colorResource(id = R.color.required_label_color))
            .padding(dimensionResource(id = R.dimen.padding_extra_small)),
        color = Color.White,
    )
}

@Composable
private fun OptionalLabel(
    modifier: Modifier = Modifier
) {
    LabelSmallText(
        text = stringResource(R.string.optional),
        modifier = modifier
            .background(colorResource(id = R.color.optional_label_color))
            .padding(dimensionResource(id = R.dimen.padding_extra_small)),
        color = Color.White,
    )
}
