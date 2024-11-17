package com.ogata_k.mobile.winp.infra.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ogata_k.mobile.winp.infra.database.entity.WorkComment
import java.time.OffsetDateTime

@Dao
interface WorkCommentDao {
    /**
     * タスクのコメント一覧を作成日時順の逆順に取得
     */
    @Query(
        """
        SELECT work_comments.* from work_comments
        WHERE work_id = :workId
        ORDER BY datetime(created_at) DESC
    """
    )
    suspend fun fetchAllWorkCommentsOrderByCreatedAtDesc(workId: Long): List<WorkComment>

    /**
     * タスクのコメントを新規登録する
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkComment(comment: WorkComment)

    /**
     * コメントを指定して更新
     */
    @Query("UPDATE work_comments SET comment = :comment, modified_at = :modifiedAt WHERE work_comment_id = :workCommentId")
    suspend fun updateWorkComment(workCommentId: Long, comment: String, modifiedAt: OffsetDateTime?)
}