package com.ogata_k.mobile.winp.infra.database.dao.impl

import com.ogata_k.mobile.winp.domain.model.work.WorkTodo
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.SummaryWorkDao
import com.ogata_k.mobile.winp.infra.database.entity.WorkComment
import com.ogata_k.mobile.winp.infra.database.with_relatioin.WorkWithWorkTodo
import java.time.OffsetDateTime
import com.ogata_k.mobile.winp.domain.infra.database.dao.SummaryWorkDao as DomainSummaryWorkDao
import com.ogata_k.mobile.winp.domain.model.work.Summary as DomainSummary
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork
import com.ogata_k.mobile.winp.domain.model.work.WorkComment as DomainWorkComment

class ISummaryWorkDao(private val db: AppDatabase, private val dao: SummaryWorkDao) :
    DomainSummaryWorkDao {
    override suspend fun getSummary(from: OffsetDateTime, to: OffsetDateTime): DomainSummary {
        val now = OffsetDateTime.now()
        val works = dao.fetchWorksAtTheRange(from, to)
        val referenceWorks: MutableMap<Long, DomainWork> = mutableMapOf()
        val uncompletedWorkIds: MutableList<Long> = mutableListOf()
        val expiredUncompletedWorkIds: MutableList<Long> = mutableListOf()
        val completedWorkIds: MutableList<Long> = mutableListOf()
        val expiredCompletedWorkIds: MutableList<Long> = mutableListOf()
        works.forEach {
            val work = toDomainWork(it)
            val workId = work.workId
            val workEndedAt = work.endedAt
            val workCompletedAt = work.completedAt

            referenceWorks[workId] = work

            if (workCompletedAt == null) {
                uncompletedWorkIds.add(workId)
                // 期限過ぎでも未完了の場合は別途集計
                if (workEndedAt != null && workEndedAt < now) {
                    expiredUncompletedWorkIds.add(workId)
                }
            } else {
                completedWorkIds.add(workId)
                // 期限過ぎで完了している場合は別途集計
                if (workEndedAt != null && workEndedAt < workCompletedAt) {
                    expiredCompletedWorkIds.add(workId)
                }
            }
        }

        val notFetchedWorkIds: MutableList<Long> = mutableListOf()
        val postedWorkComments: List<DomainWorkComment> =
            dao.fetchWorkCommentsAtTheRange(from, to).map {
                val workComment = toDomainWorkComment(it)
                // 取得済みのWorkでなければ取得対象として記録しておく
                if (!referenceWorks.containsKey(workComment.workId)) {
                    notFetchedWorkIds.add(workComment.workId)
                }
                return@map workComment
            }

        // 最初に取得したWorkに存在しないものを追加で取得
        dao.fetchWorksByWorkIds(notFetchedWorkIds).forEach {
            referenceWorks[it.work.workId] = toDomainWork(it)
        }

        return DomainSummary(
            from = from,
            to = to,
            referenceWorks = referenceWorks.toMap(),
            uncompletedWorkIds = uncompletedWorkIds.toList(),
            expiredUncompletedWorkIds = expiredUncompletedWorkIds.toList(),
            completedWorkIds = completedWorkIds.toList(),
            expiredCompletedWorkIds = expiredCompletedWorkIds.toList(),
            postedComments = postedWorkComments.toList(),
        )
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
            WorkTodo(
                workTodoId = it.workTodoId,
                description = it.description,
                completedAt = it.completedAt,
                createdAt = it.createdAt,
            )
        },
    )
}

private fun toDomainWorkComment(workComment: WorkComment): DomainWorkComment {
    return DomainWorkComment(
        workCommentId = workComment.workCommentId,
        workId = workComment.workId,
        comment = workComment.comment,
        modifiedAt = workComment.modifiedAt,
        createdAt = workComment.createdAt,
    )
}
