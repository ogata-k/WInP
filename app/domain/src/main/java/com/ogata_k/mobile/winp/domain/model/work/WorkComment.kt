package com.ogata_k.mobile.winp.domain.model.work

import java.time.OffsetDateTime

/**
 * タスクの進捗のコメント
 */
data class WorkComment(
    val workCommentId: Long,
    val workId: Long,
    val comment: String,
    val modifiedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
)
