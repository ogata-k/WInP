package com.ogata_k.mobile.winp.infra.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ogata_k.mobile.winp.infra.database.entity.WorkComment
import com.ogata_k.mobile.winp.infra.database.with_relatioin.WorkWithWorkTodo
import java.time.OffsetDateTime

@Dao
interface SummaryWorkDao {
    /**
     * 指定した範囲で対応すべきタスクを検索する（リレーションで取得する進捗状況のソートはなし）
     * 外部で使う予定のメソッドではないので、_から始まる名前にしてある。
     *
     * ※ 基本的にWorkWithWorkTodoDao._fetchWorksAtTheRangeWithoutSortRelation()と同じ条件だが、ページの指定とソート条件の指定が違う。これはサマリーでは基本的に昇順で扱うなどの違いがあるため。
     *
     * 抽出条件
     * ・期限が指定期間に含まれるか
     * ・完了していてタスクの開始日時～タスクの完了日時が指定期間に含まれるか
     * ・完了していなくて期限が過ぎているか
     */
    @Transaction
    @Query(
        """
            SELECT works.* FROM works WHERE
        """ +
                //
                // 期限が指定期間に含まれるか
                //
                """
            (began_at IS NULL AND ended_at IS NULL)
            OR (datetime(began_at) <= datetime(:to) AND ended_at IS NULL)
            OR (began_at IS NULL AND datetime(ended_at) >= datetime(:from))
            OR (datetime(began_at) <= datetime(:to) AND datetime(ended_at) >= datetime(:from))
        """ +
                //
                // 完了していてタスクの開始日時～タスクの完了日時が指定期間に含まれるか
                //
                """
            OR (completed_at IS NOT NULL AND (
                (began_at IS NULL AND datetime(completed_at) >= datetime(:from))
                OR (datetime(began_at) <= datetime(:to) AND datetime(completed_at) >= datetime(:from))
            ))
        """ +
                //
                // 完了していなくて期限が過ぎているか
                //
                """
            OR (completed_at IS NULL AND datetime(ended_at) <= datetime(:from))
        """ +
                //
                // その他条件
                //
                """
            ORDER BY began_at IS NULL ASC, datetime(began_at) ASC, ended_at IS NULL ASC, datetime(ended_at) ASC,
            work_id ASC
        """
    )
    suspend fun _fetchWorksAtTheRangeWithoutSortRelation(
        from: OffsetDateTime, to: OffsetDateTime
    ): List<WorkWithWorkTodo>

    /**
     * 指定した範囲で対応すべきタスクを検索する
     */
    suspend fun fetchWorksAtTheRange(
        from: OffsetDateTime, to: OffsetDateTime
    ): List<WorkWithWorkTodo> {
        val works = _fetchWorksAtTheRangeWithoutSortRelation(from, to)
        return works.map { work -> work.copy(workTodos = work.workTodos.sortedBy { it.position }) }
    }

    /**
     * 指定したworkIdの一覧で対応すべきタスクを検索する（リレーションで取得する進捗状況のソートはなし）
     * 外部で使う予定のメソッドではないので、_から始まる名前にしてある。
     *
     * ※ 基本的にworkIdの一覧による指定以外はWorkWithWorkTodoDao._fetchWorksAtTheRangeWithoutSortRelation()と同じ条件だが、ページの指定とソート条件の指定が違う。これはサマリーでは基本的に昇順で扱うなどの違いがあるため。
     */
    @Transaction
    @Query(
        """
            SELECT works.* FROM works
            WHERE works.work_id IN (:workIds)
            ORDER BY began_at IS NULL ASC, datetime(began_at) ASC, ended_at IS NULL ASC, datetime(ended_at) ASC,
            work_id ASC
        """
    )
    suspend fun _fetchWorksByWorkIdsWithoutSortRelation(workIds: List<Long>): List<WorkWithWorkTodo>

    /**
     * 指定したworkId一覧で対応すべきタスクを検索する
     */
    suspend fun fetchWorksByWorkIds(workIds: List<Long>): List<WorkWithWorkTodo> {
        val works = _fetchWorksByWorkIdsWithoutSortRelation(workIds)
        return works.map { work -> work.copy(workTodos = work.workTodos.sortedBy { it.position }) }
    }

    /**
     * タスクのコメント一覧を作成日時順の昇順に取得。サマリーでWorkが取得できないのは困るので、joinして問題ないようにしておく。
     * ※これもWorkCommentDaoのfetchAllWorkCommentsOrderByCreatedAtDesc()とソート条件が違う。
     */
    @Query(
        """
        SELECT work_comments.* FROM work_comments
        INNER JOIN works ON works.work_id = work_comments.work_id
        WHERE datetime(work_comments.created_at) <= datetime(:to) AND datetime(work_comments.created_at) >= datetime(:from)
        ORDER BY datetime(work_comments.created_at) ASC, work_comments.work_comment_id ASC
    """
    )
    suspend fun fetchWorkCommentsAtTheRange(
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<WorkComment>
}