package com.ogata_k.mobile.winp.infra.database.dao.impl

import androidx.room.withTransaction
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.WorkWithWorkTodoDao
import com.ogata_k.mobile.winp.infra.database.entity.Work
import com.ogata_k.mobile.winp.infra.database.entity.WorkTodo
import com.ogata_k.mobile.winp.infra.database.with_relatioin.WorkWithWorkTodo
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Optional
import com.ogata_k.mobile.winp.domain.infra.database.dao.WorkDao as DomainWorkDao
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork
import com.ogata_k.mobile.winp.domain.model.work.WorkTodo as DomainWorkTodo

class IWorkDao(private val db: AppDatabase, private val dao: WorkWithWorkTodoDao) : DomainWorkDao {
    override suspend fun fetchPageWorksAtTheRange(
        from: OffsetDateTime,
        to: OffsetDateTime,
        itemOffset: Int,
        pageSize: Int
    ): List<DomainWork> {
        return dao.fetchWorksAtTheRange(from, to, itemOffset, pageSize).map {
            toDomainWork(it)
        }
    }

    override suspend fun findWork(workId: Long): Optional<DomainWork> {
        return dao.findWork(workId).map {
            toDomainWork(it)
        }
    }

    override suspend fun insertWork(work: DomainWork) {
        db.withTransaction {
            val formattedData = splitWorkData(work)
            val createdWorkId = dao.insertWork(formattedData.work)
            // 作成時は対応項目のworkIdが不整な値になっているので保存して取得できた正しい値に置き換え
            dao.insertWorkTodos(formattedData.createWorkTodos.map { it.copy(workId = createdWorkId) })
            // タスク作成時は更新はない
            if (formattedData.updateWorkTodos.isNotEmpty()) {
                throw IllegalArgumentException("illegal work todos ${work.workTodos} for create work")
            }
        }
    }

    override suspend fun updateWork(work: DomainWork) {
        db.withTransaction {
            val formattedData = splitWorkData(work)
            dao.updateWork(formattedData.work)
            val validWorkTodoIds = formattedData.updateWorkTodos.map { it.workTodoId }
            dao.deleteWorkTodos(formattedData.work.workId, validWorkTodoIds)
            dao.insertWorkTodos(formattedData.createWorkTodos)
            dao.updateWorkTodos(formattedData.updateWorkTodos)
        }
    }

    override suspend fun updateTaskState(workTodoId: Long, completedAt: OffsetDateTime?) {
        db.withTransaction {
            dao.updateTaskState(workTodoId, completedAt)
        }
    }

    override suspend fun deleteWork(work: DomainWork) {
        db.withTransaction {
            dao.deleteWork(work.workId)
        }
    }
}

private fun toDomainWork(work: WorkWithWorkTodo): DomainWork {
    return DomainWork(
        workId = work.work.workId,
        title = work.work.title,
        description = work.work.description,
        beganAt = work.work.beganAt,
        endedAt = work.work.endedAt,
        completedAt = work.work.completedAt,
        createdAt = work.work.createdAt,
        workTodos = work.workTodos.map {
            DomainWorkTodo(
                workTodoId = it.workTodoId,
                description = it.description,
                completedAt = it.completedAt,
                createdAt = it.createdAt,
            )
        },
    )
}

/**
 * 保存用に成形したWorkデータ
 */
private data class FormattedWorkDataForSave(
    val work: Work,
    val createWorkTodos: List<WorkTodo>,
    val updateWorkTodos: List<WorkTodo>,
)

private fun splitWorkData(work: DomainWork): FormattedWorkDataForSave {
    val now = LocalDateTimeConverter.toOffsetDateTime(LocalDateTime.now())
    val workCreatedAt: OffsetDateTime =
        if (work.workId == AsCreate.CREATING_ID) now else work.createdAt
    val workId = work.workId
    val workForSave = Work(
        workId = workId,
        title = work.title,
        description = work.description,
        beganAt = work.beganAt,
        endedAt = work.endedAt,
        completedAt = work.completedAt,
        createdAt = workCreatedAt,
    )

    val createWorkTodos: MutableList<WorkTodo> = mutableListOf()
    val updateWorkTodos: MutableList<WorkTodo> = mutableListOf()
    work.workTodos.forEachIndexed { index, item ->
        val workTodoCreatedAt = if (work.workId == AsCreate.CREATING_ID) now else item.createdAt
        val workTodoForSave = WorkTodo(
            workTodoId = item.workTodoId,
            workId = workId,
            description = item.description,
            completedAt = item.completedAt,
            position = index,
            createdAt = workTodoCreatedAt,
        )

        if (item.workTodoId == AsCreate.CREATING_ID) {
            createWorkTodos.add(workTodoForSave)
        } else {
            updateWorkTodos.add(workTodoForSave)
        }
    }

    return FormattedWorkDataForSave(
        work = workForSave,
        createWorkTodos = createWorkTodos.toList(),
        updateWorkTodos = updateWorkTodos.toList(),
    )
}
