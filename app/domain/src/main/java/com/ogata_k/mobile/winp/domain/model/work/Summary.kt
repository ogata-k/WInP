package com.ogata_k.mobile.winp.domain.model.work

import java.time.OffsetDateTime

/**
 * 指定した期間でタスクや投稿されたコメントを要約できるモデル
 */
class Summary(
    // クエリ期間
    val from: OffsetDateTime,
    val to: OffsetDateTime,
    // Summaryの対象となるタスクの参照データ。KeyはworkId。
    val referenceWorks: Map<Long, Work>,
    // 未完了タスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val uncompletedWorkIds: List<Long>,
    // 未完了のうち期限切れのタスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val expiredUncompletedWorkIds: List<Long>,
    // 完了タスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val completedWorkIds: List<Long>,
    // 完了しているけど期限切れのタスクのworkIdの一覧。期限の条件が厳しい順で、同等ならworkIdの昇順。
    val expiredCompletedWorkIds: List<Long>,
    val postedComments: List<WorkComment>,
)