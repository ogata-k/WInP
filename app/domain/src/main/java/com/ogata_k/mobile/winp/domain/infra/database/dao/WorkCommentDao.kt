package com.ogata_k.mobile.winp.domain.infra.database.dao

import com.ogata_k.mobile.winp.domain.model.work.WorkComment
import java.time.OffsetDateTime

/**
 * タスクのコメントDAO
 */
interface WorkCommentDao {
    /**
     * タスクのコメント一覧を取得
     */
    suspend fun fetchAllWorkCommentsOrderByCreatedAtDesc(workId: Long): List<WorkComment>

    /**
     * タスクのコメントを新規登録する
     */
    suspend fun insertWorkComment(comment: WorkComment)

    /**
     * コメントを指定して更新
     */
    suspend fun updateWorkComment(workCommentId: Long, comment: String, modifiedAt: OffsetDateTime)

    /**
     * タスクのコメントを削除する
     */
    suspend fun deleteWorkComment(comment: WorkComment)
}