package com.ogata_k.mobile.winp.infra.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "work_todos",
    indices = [
        Index(value = ["work_id"]),
    ],
)
data class WorkTodo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "work_todo_id")
    val workTodoId: Long,

    @ColumnInfo(name = "work_id")
    val workId: Long,

    @ColumnInfo
    val description: String,

    @ColumnInfo(name = "completed_at")
    val completedAt: OffsetDateTime?,

    @ColumnInfo
    val position: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime,
)