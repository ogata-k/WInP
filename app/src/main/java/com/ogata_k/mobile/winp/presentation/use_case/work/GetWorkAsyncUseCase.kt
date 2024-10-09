package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.model.work.Work
import com.ogata_k.mobile.winp.domain.model.work.WorkTodo
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkOutput
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class IGetWorkAsyncUseCase : GetWorkAsyncUseCase {
    override suspend fun call(input: GetWorkInput): GetWorkOutput {
        // @todo 実際の実装にする
        delay(500)
        val todoItems: MutableList<WorkTodo> = mutableListOf()
        (0..5).forEach { index ->
            todoItems.add(
                WorkTodo(
                    id = index + 1,
                    description = "対応するTODO%d".format(index + 1),
                    completedAt = if (index % 3 == 0) LocalDateTime.now() else null,
                )
            )
        }

        val now = LocalDateTime.now()
        return GetWorkOutput.success(
            Work(
                id = input.workId,
                title = "編集可能",
                description = "これは${now}に作成された、作成済みのタスクの説明です。",
                beganAt = if (input.workId % 8 == 0) now.minusSeconds(200.toLong()) else null,
                endedAt = if (input.workId % 23 == 0) now.minusSeconds(100.toLong())
                else if (input.workId % 7 == 0) now.plusDays(2)
                else null,
                completedAt = if (input.workId % 3 == 0) LocalDateTime.now() else null,
                workTodos = if (input.workId % 5 == 0) todoItems else emptyList(),
            )
        )
    }
}