package com.ogata_k.mobile.winp.infra.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ogata_k.mobile.winp.infra.database.entity.Work
import com.ogata_k.mobile.winp.infra.database.entity.WorkTodo
import com.ogata_k.mobile.winp.infra.database.with_relatioin.WorkWithWorkTodo
import java.time.OffsetDateTime
import java.util.Optional

@Dao
interface WorkWithWorkTodoDao {
    /**
     * 指定した範囲で対応すべきタスクを検索する（リレーションで取得する進捗状況のソートはなし）
     * 外部で使う予定のメソッドではないので、_から始まる名前にしてある。
     */
    @Transaction
    @Query(
        """
        SELECT works.* FROM works
        WHERE (began_at IS NULL AND ended_at IS NULL) 
            OR (datetime(began_at) <= datetime(:to) AND ended_at IS NULL)
            OR (began_at IS NULL AND datetime(ended_at) >= datetime(:from))
            OR (datetime(began_at) <= datetime(:to) AND datetime(ended_at) >= datetime(:from))
        ORDER BY completed_at IS NULL DESC, datetime(completed_at) DESC, work_id DESC
        LIMIT :pageSize OFFSET :offset
        """
    )
    suspend fun _fetchWorksAtTheRangeWithoutSortRelation(
        from: OffsetDateTime,
        to: OffsetDateTime,
        offset: Int,
        pageSize: Int
    ): List<WorkWithWorkTodo>

    /**
     * 指定した範囲で対応すべきタスクを検索する
     */
    suspend fun fetchWorksAtTheRange(
        from: OffsetDateTime,
        to: OffsetDateTime,
        offset: Int,
        pageSize: Int
    ): List<WorkWithWorkTodo> {
        val works = _fetchWorksAtTheRangeWithoutSortRelation(from, to, offset, pageSize)
        return works.map { work -> work.copy(workTodos = work.workTodos.sortedBy { it.position }) }
    }

    /**
     * 指定したwork_idでタスクを検索する（リレーションで取得する進捗状況のソートはなし）
     * 外部で使う予定のメソッドではないので、_から始まる名前にしてある。
     */
    @Transaction
    @Query("SELECT works.* FROM works WHERE work_id = :workId")
    suspend fun _findWorkWithoutSortRelation(workId: Long): Optional<WorkWithWorkTodo>

    /**
     * 指定したwork_idでタスクを検索する
     */
    suspend fun findWork(workId: Long): Optional<WorkWithWorkTodo> {
        val works = _findWorkWithoutSortRelation(workId)
        return works.map { work -> work.copy(workTodos = work.workTodos.sortedBy { it.position }) }
    }

    /**
     * タスクを新規登録する
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWork(work: Work): Long

    /**
     * タスクのタスクTODOを新規登録する
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkTodos(workTodos: List<WorkTodo>): List<Long>

    /**
     * タスクを更新する
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWork(work: Work)

    /**
     * タスクのタスクTODOを更新する
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWorkTodos(workTodos: List<WorkTodo>)

    /**
     * タスクTODOのIDを指定して更新
     */
    @Query("UPDATE work_todos SET completed_at = :completedAt WHERE work_todo_id = :workTodoId")
    suspend fun updateTaskState(workTodoId: Long, completedAt: OffsetDateTime?)

    /**
     * タスクを削除する
     */
    @Query("DELETE FROM works where work_id = :workId")
    suspend fun _deleteWork(workId: Long)

    /**
     * タスクを削除する
     */
    @Query("DELETE FROM works where work_id = :workId")
    suspend fun deleteWork(workId: Long) {
        _deleteWork(workId)
        deleteAllWorkTodo(workId)
    }

    /**
     * タスクを削除する
     */
    @Query("DELETE FROM work_todos where work_id = :workId")
    suspend fun deleteAllWorkTodo(workId: Long)

    /**
     * タスクを削除する
     */
    @Query("DELETE FROM work_todos where work_id = :workId AND work_todo_id NOT IN (:validWorkTodoIds)")
    suspend fun deleteWorkTodos(workId: Long, validWorkTodoIds: List<Long>)
}