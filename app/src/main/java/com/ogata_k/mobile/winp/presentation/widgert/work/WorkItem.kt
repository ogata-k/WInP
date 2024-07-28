package com.ogata_k.mobile.winp.presentation.widgert.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildFullDateTimePatternFormatter
import com.ogata_k.mobile.winp.presentation.model.wip.Work
import com.ogata_k.mobile.winp.presentation.theme.WInPTheme
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.BodySmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleLargeText
import java.time.LocalDateTime

@Composable
fun WorkItem(work: Work, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = if (!work.hasPeriod) CardDefaults.cardColors().copy(
            containerColor = colorResource(id = R.color.pending_work_item),
            contentColor = contentColorFor(colorResource(id = R.color.pending_work_item)),
        )
        else if (!work.isCompleted && work.isExpired) CardDefaults.cardColors().copy(
            containerColor = colorResource(id = R.color.expired_work_item),
            contentColor = contentColorFor(colorResource(id = R.color.expired_work_item)),
        )
        else CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (work.isCompleted) Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = colorResource(id = R.color.completed_check),
                        modifier = Modifier
                            .padding(end = dimensionResource(id = R.dimen.padding_small))
                            .size((MaterialTheme.typography.bodySmall.fontSize.value * 1.2).dp)
                    )
                    val formattedPair =
                        work.splitToFormattedPeriod(buildFullDateTimePatternFormatter())
                    BodySmallText(
                        if (work.hasPeriod)
                            stringResource(
                                id = R.string.period_with_range,
                                formattedPair.first,
                                formattedPair.second
                            )
                        else
                            stringResource(id = R.string.none_period),
                    )
                }
                TitleLargeText(
                    work.title,
                    fontWeight = FontWeight.Bold,
                )
                BodyMediumText(
                    work.description,
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.padding_medium),
                    ),
                )
            }
            Icon(
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.padding_medium),
                ),
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = stringResource(id = R.string.navigate_to_detail)
            )
        }
    }
}

@Preview
@Composable
private fun WorkItemNoDeadlinePreview() {
    val work = Work(
        id = 1,
        title = "サンプルタスク",
        description = "これはサンプル。完了もしていないし期限もなし。",
        beganAt = LocalDateTime.now(),
        deadline = null,
        completedAt = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkItem(work) {}
    }
}

@Preview
@Composable
private fun WorkItemNoPeriodPreview() {
    val work = Work(
        id = 1,
        title = "サンプルタスク",
        description = "これはサンプル。完了もしていないし期限もなし。",
        beganAt = null,
        deadline = null,
        completedAt = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkItem(work) {}
    }
}

@Preview
@Composable
private fun NotCompletedNotExpiredWorkItemPreview() {
    val work = Work(
        id = 1,
        title = "サンプルタスク",
        description = "これはサンプル。期限は設けられているがまだ来ていない。",
        beganAt = LocalDateTime.now(),
        deadline = LocalDateTime.now().plusDays(1),
        completedAt = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkItem(work) {}
    }
}

@Preview
@Composable
private fun NotCompletedExpiredWorkItemPreview() {
    val work = Work(
        id = 1,
        title = "サンプルタスク",
        description = "これはサンプル。期限は設けられているがまだ来ていない。",
        beganAt = LocalDateTime.now().minusDays(2),
        deadline = LocalDateTime.now().minusDays(1),
        completedAt = null,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkItem(work) {}
    }
}

@Preview
@Composable
private fun CompletedWorkItemPreview() {
    val work = Work(
        id = 1,
        title = "サンプルタスク",
        description = "これはサンプル。タスクは完了している。",
        beganAt = LocalDateTime.now().minusDays(1),
        deadline = LocalDateTime.now().minusDays(1),
        completedAt = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    WInPTheme {
        WorkItem(work) {}
    }
}
