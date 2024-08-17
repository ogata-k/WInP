package com.ogata_k.mobile.winp.presentation.widgert.work_form

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormData
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyMediumText
import java.util.UUID

@Composable
fun WorkTodoFormColumnItem(
    todoFormData: WorkTodoFormData,
    modifier: Modifier = Modifier,
) {
    val checkIconSize = dimensionResource(id = R.dimen.icon_size_medium)

    val cutSize = with(LocalDensity.current) {
        checkIconSize.toPx() / 2
    }
    val shape = GenericShape { size, _ ->
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(cutSize, size.height)
        lineTo(0f, size.height - cutSize)
        lineTo(0f, size.height)
    }

    val isCompleted: Boolean = todoFormData.isCompleted
    Card(
        // 角が角張った長方形を想定し、Modifierでチケット風に調整する
        shape = RectangleShape,
        modifier = Modifier
            .clip(shape)
            .then(modifier),
        colors = if (isCompleted)
            CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.completed_work_todo),
                contentColor = contentColorFor(colorResource(id = R.color.completed_work_todo)),
            )
        else
            CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.not_completed_work_todo),
                contentColor = contentColorFor(colorResource(id = R.color.not_completed_work_todo)),
            ),
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(
                    vertical = dimensionResource(id = R.dimen.padding_medium),
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(checkIconSize),
                imageVector = Icons.Filled.Check,
                contentDescription = if (isCompleted) stringResource(id = R.string.completed_work_todo)
                else stringResource(id = R.string.not_completed_work_todo),
                // 未完了なら背景色と同じ色を指定して、実質非表示とする。
                tint = if (isCompleted)
                    colorResource(id = R.color.completed_check)
                else
                    colorResource(id = R.color.not_completed_work_todo),
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_large)))
            VerticalDivider(color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_large)))
            BodyMediumText(
                text = todoFormData.description,
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                    .weight(1f),
            )
        }
    }
}

@Preview
@Composable
private fun NotCompletedWorkTodoFormColumnItemPreview() {
    val todoFormData = WorkTodoFormData(
        uuid = UUID.randomUUID(),
        id = null,
        description = "未完了のタスクTODOアイテム\n少なくともこれはやっておかなければいけないので忘れずに行うこと。",
        isCompleted = false,
    )

    WorkTodoFormColumnItem(todoFormData)
}

@Preview
@Composable
private fun CompletedWorkTodoFormColumnItemPreview() {
    val todoFormData = WorkTodoFormData(
        uuid = UUID.randomUUID(),
        id = null,
        description = "完了済みのタスクTODOアイテム\n少なくともこれはやっておかなければいけないので忘れずに行うこと。",
        isCompleted = true,
    )

    WorkTodoFormColumnItem(todoFormData)
}

@Preview(fontScale = 2.0f)
@Composable
private fun LargeTextCompletedWorkTodoFormColumnItemPreview() {
    val todoFormData = WorkTodoFormData(
        uuid = UUID.randomUUID(),
        id = null,
        description = "完了済みのタスクTODOアイテム",
        isCompleted = true,
    )

    WorkTodoFormColumnItem(todoFormData)
}

@Preview(fontScale = 0.5f)
@Composable
private fun SmallTextCompletedWorkTodoFormColumnItemPreview() {
    val todoFormData = WorkTodoFormData(
        uuid = UUID.randomUUID(),
        id = null,
        description = "完了済みのタスクTODOアイテム",
        isCompleted = true,
    )

    WorkTodoFormColumnItem(todoFormData)
}
