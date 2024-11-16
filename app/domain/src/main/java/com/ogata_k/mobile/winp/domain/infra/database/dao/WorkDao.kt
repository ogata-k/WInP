package com.ogata_k.mobile.winp.domain.infra.database.dao

import com.ogata_k.mobile.winp.domain.model.work.Work
import java.time.OffsetDateTime
import java.util.Optional

/**
 * タスクのDAO
 */
interface WorkDao {
    /**
     * 指定した範囲で対応すべきタスクを検索する
     */
    suspend fun fetchPageWorksAtTheRange(
        from: OffsetDateTime,
        to: OffsetDateTime,
        itemOffset: Int,
        pageSize: Int
    ): List<Work>

    /**
     * 指定したwork_idでタスクを検索する
     */
    suspend fun findWork(workId: Long): Optional<Work>

    /**
     * タスクを新規登録する
     */
    suspend fun insertWork(work: Work)

    /**
     * タスクを更新する
     */
    suspend fun updateWork(work: Work)

    /**
     * 対応項目のIDを指定して更新
     */
    suspend fun updateTaskState(workTodoId: Long, completedAt: OffsetDateTime?)

    /**
     * タスクを削除する
     */
    suspend fun deleteWork(work: Work)
}