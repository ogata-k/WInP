package com.ogata_k.mobile.winp.presentation.widgert.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullDateTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.theme.WInPTheme
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.BodySmallText
import java.time.LocalDateTime

@Composable
fun WorkCommentItem(
    comment: WorkComment,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        BodyMediumText(
            text = comment.comment,
        )
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_medium_large)))
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.Bottom,
        ) {
            if (comment.isModified) {
                BodySmallText(
                    text = stringResource(R.string.modified_paren_label)
                            + stringResource(R.string.extra_small_separator),
                )
            }

            BodySmallText(
                text = formatFullDateTimeOrEmpty(comment.createdAt),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview
@Composable
private fun NotModifiedWorkCommentItemPreview() {
    val comment = WorkComment(
        workCommentId = 1,
        comment = "これは編集されていないコメントです",
        modifiedAt = null,
        createdAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkCommentItem(comment)
    }
}

@Preview
@Composable
private fun ModifiedWorkCommentItemPreview() {
    val comment = WorkComment(
        workCommentId = 2,
        comment = "これは編集されているコメントです",
        modifiedAt = LocalDateTime.now().plusMinutes(30),
        createdAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkCommentItem(comment)
    }
}