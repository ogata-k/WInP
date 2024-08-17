package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.ogata_k.mobile.winp.R

/**
 * ラベル付きRadioボタン
 */
@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: (() -> Unit)?,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelExpand: Boolean = true,
    label: @Composable (text: String, modifier: Modifier) -> Unit = { labelText: String, labelModifier: Modifier ->
        LabelMediumText(
            text = labelText,
            modifier = labelModifier
        )
    }
) {
    Row(
        modifier = if (onClick == null) modifier else {
            Modifier
                .clickable { onClick() }
                .then(modifier)
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled,
        )
        var labelModifier: Modifier = Modifier
        if (labelExpand) {
            labelModifier = labelModifier.weight(1f)
        }
        label(text, labelModifier)
        Spacer(Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
    }
}