package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import java.time.LocalDateTime
import com.ogata_k.mobile.winp.domain.model.work.Summary as DomainSummary

data class WorkSummary(
    // クエリ期間
    val from: LocalDateTime,
    val to: LocalDateTime,
    // Summaryの対象となるタスクの参照データ。KeyはworkId。
    val referenceWorks: Map<Long, Work>,
    // 未完了タスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val uncompletedWorkIds: List<Long>,
    // 未完了のうち期限切れのタスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val expiredUncompletedWorkIds: List<Long>,
    // 完了タスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val completedWorkIds: List<Long>,
    // 対応が完了しているけど期限切れのタスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val expiredCompletedWorkIds: List<Long>,
    val postedComments: List<WorkComment>,
) {
    companion object : FromDomain<DomainSummary, WorkSummary> {
        override fun fromDomainModel(domain: DomainSummary): WorkSummary {
            return WorkSummary(
                from = LocalDateTimeConverter.fromOffsetDateTime(domain.from),
                to = LocalDateTimeConverter.fromOffsetDateTime(domain.to),
                referenceWorks = domain
                    .referenceWorks
                    .map { it.key to Work.fromDomainModel(it.value) }
                    .toMap(),
                uncompletedWorkIds = domain.uncompletedWorkIds,
                expiredUncompletedWorkIds = domain.expiredUncompletedWorkIds,
                completedWorkIds = domain.completedWorkIds,
                expiredCompletedWorkIds = domain.expiredCompletedWorkIds,
                postedComments = domain.postedComments.map { WorkComment.fromDomainModel(it) },
            )
        }

        fun empty(from: LocalDateTime, to: LocalDateTime): WorkSummary {
            return WorkSummary(
                from = from,
                to = to,
                referenceWorks = mapOf(),
                uncompletedWorkIds = listOf(),
                expiredUncompletedWorkIds = listOf(),
                completedWorkIds = listOf(),
                expiredCompletedWorkIds = listOf(),
                postedComments = listOf(),
            )
        }
    }

    /**
     * 未完了タスクの個数
     */
    fun countUncompletedWork(): Int {
        return uncompletedWorkIds.count()
    }

    /**
     * 期限切れ未完了タスクの個数
     */
    fun countExpiredUncompletedWork(): Int {
        return expiredUncompletedWorkIds.count()
    }

    /**
     * 完了したタスクの個数
     */
    fun countCompletedWork(): Int {
        return completedWorkIds.count()
    }

    /**
     * 期限切れの完了したタスクの個数
     */
    fun countExpiredCompletedWork(): Int {
        return expiredCompletedWorkIds.count()
    }
}
